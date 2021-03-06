package com.yazantarifi.utils

import com.android.ddmlib.IDevice
import com.android.ddmlib.NullOutputReceiver
import com.android.tools.idea.gradle.project.sync.GradleSyncState
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiPackage
import com.yazantarifi.impl.AndroidDebugBridgeManagerImplementation
import com.yazantarifi.models.AndroidDebugEvent
import org.jetbrains.android.sdk.AndroidSdkUtils


class AndroidDebugBridgeManager constructor(private val project: Project): AndroidDebugBridgeManagerImplementation {

    private val notificationsManager: IdeaNotificationsManager by lazy { IdeaNotificationsManager(project) }
    companion object {
        private const val NO_CONNECTED_DEVICES_MESSAGE = "No Connected Devices ..."
        private const val ADB_GRADLE_SYNC = "Couldn't determine if a gradle sync is in progress"
        private const val GRADLE_SYNC_IN_PROGRESS = "Gradle Sync In Progress .. Can't Submit Action While Sync Execution"
        private const val ADB_TITLE = "ADB Events"
    }

    override fun onDebugEventTriggered(event: AndroidDebugEvent) {
        val connectedDevices = AndroidSdkUtils.getDebugBridge(project)?.devices
        if (connectedDevices.isNullOrEmpty()) {
            notificationsManager.showNotification(ADB_TITLE, NO_CONNECTED_DEVICES_MESSAGE)
            return
        }

        if (isGradleSyncExecution()) {
            notificationsManager.showNotification(ADB_TITLE, GRADLE_SYNC_IN_PROGRESS)
            return
        }

        connectedDevices.forEach {
            executeEvent(event, it)
            notificationsManager.showNotification(ADB_TITLE, "ADB Event Executed for Device : ${it.name}")
        }
    }

    override fun executeEvent(event: AndroidDebugEvent, device: IDevice) {
        when (event) {
            AndroidDebugEvent.SHOW_LAYOUT_BOUNDS -> toggleLayoutBounds(true, device)
            AndroidDebugEvent.HIDE_LAYOUT_BOUNDS -> toggleLayoutBounds(false, device)
            AndroidDebugEvent.ENABLE_DONT_KEEP_ACTIVITIES -> toggleDontKeepActivities(true, device)
            AndroidDebugEvent.DISABLE_DONT_KEEP_ACTIVITIES -> toggleDontKeepActivities(false, device)
            AndroidDebugEvent.SHOW_OVERDRAW_AREAS -> toggleOverdrawAreas(false, device)
            AndroidDebugEvent.HIDE_OVERDRAW_AREAS -> toggleOverdrawAreas(false, device)
            AndroidDebugEvent.UN_INSTALL_APPLICATION -> unInstallApplication(device)
            AndroidDebugEvent.CLEAR_DATA_AND_RESTART_APPLICATION -> clearDataAndRestartApplication(device)
            AndroidDebugEvent.CLEAR_DATA_APPLICATION -> clearDataApplication(device)
            AndroidDebugEvent.REMOVE_APPLICATION_PERMISSIONS -> removeApplicationPermissions(device)
            AndroidDebugEvent.KILL_APPLICATION_ACTION -> killApplication(device)
            AndroidDebugEvent.FORCE_STOP_APPLICATION -> forceStopApplication(device)
            AndroidDebugEvent.SHOW_TAPS_OPTION -> toggleShowTapsOption(true, device)
            AndroidDebugEvent.HIDE_TAPS_OPTION -> toggleShowTapsOption(false, device)
            AndroidDebugEvent.SHOW_POINTER_LOCATION -> togglePointerLocation(true, device)
            AndroidDebugEvent.HIDE_POINTER_LOCATION -> togglePointerLocation(false, device)
        }
    }

    override fun togglePointerLocation(isEnabled: Boolean, device: IDevice) {
        when (isEnabled) {
            true -> device.executeShellCommand("settings put system pointer_location 1", NullOutputReceiver())
            false -> device.executeShellCommand("settings put system pointer_location 0", NullOutputReceiver())
        }
    }

    override fun toggleShowTapsOption(isEnabled: Boolean, device: IDevice) {
        when (isEnabled) {
            true -> device.executeShellCommand("content insert --uri content://settings/system --bind name:s:show_touches --bind value:i:1", NullOutputReceiver())
            false -> device.executeShellCommand("content insert --uri content://settings/system --bind name:s:show_touches --bind value:i:0", NullOutputReceiver())
        }
    }

    override fun killApplication(device: IDevice) {
        getAvailablePackages().forEach {
            device.kill(it.qualifiedName)
        }
    }

    override fun toggleOverdrawAreas(isEnabled: Boolean, device: IDevice) {
        when (isEnabled) {
            true -> device.executeShellCommand("setprop debug.hwui.overdraw show", NullOutputReceiver())
            false -> device.executeShellCommand("getprop debug.hwui.overdraw false", NullOutputReceiver())
        }
    }

    override fun unInstallApplication(device: IDevice) {
        getAvailablePackages().forEach {
            device.uninstallPackage(it.qualifiedName)
        }
    }

    override fun forceStopApplication(device: IDevice) {
        getAvailablePackages().forEach {
            device.forceStop(it.qualifiedName)
        }
    }

    override fun clearDataAndRestartApplication(device: IDevice) {
        try {
            val packageName = ApplicationUtils.getPackageName(project)
            clearDataApplication(device)
            device.executeShellCommand("monkey -p $packageName -c android.intent.category.LAUNCHER 1", NullOutputReceiver())
        } catch (ex: Exception) {
            notificationsManager.showNotification(ADB_TITLE, "Failed to clear Application Data With Restart Application")
        }
    }

    override fun clearDataApplication(device: IDevice) {
        val packageName = ApplicationUtils.getPackageName(project)
        try {
            device.executeShellCommand("pm clear $packageName", NullOutputReceiver())
        } catch (ex: Exception) {
            notificationsManager.showNotification(ADB_TITLE, "Failed to clear Application Data With Package Name : $packageName")
        }
    }

    override fun removeApplicationPermissions(device: IDevice) {
        device.executeShellCommand("pm reset-permissions", NullOutputReceiver())
    }

    override fun getAvailablePackages(): HashSet<PsiPackage> {
        return ApplicationUtils.getApplicationPackages(project)
    }

    override fun isGradleSyncExecution(): Boolean {
        return try {
            GradleSyncState.getInstance(project).isSyncInProgress
        } catch (t: Throwable) {
            notificationsManager.showNotification(ADB_TITLE, ADB_GRADLE_SYNC)
            false
        }
    }

    override fun toggleDontKeepActivities(isEnabled: Boolean, device: IDevice) {
        when (isEnabled) {
            true -> device.executeShellCommand("settings put global always_finish_activities 1", NullOutputReceiver())
            false -> device.executeShellCommand("settings put global always_finish_activities 0", NullOutputReceiver())
        }
    }

    override fun toggleLayoutBounds(isEnabled: Boolean, device: IDevice) {
        when (isEnabled) {
            true -> device.executeShellCommand("setprop debug.layout true ; service call activity 1599295570", NullOutputReceiver())
            false -> device.executeShellCommand("setprop debug.layout false ; service call activity 1599295570", NullOutputReceiver())
        }
    }

}
