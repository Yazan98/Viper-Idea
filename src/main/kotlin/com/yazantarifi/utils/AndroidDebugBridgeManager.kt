package com.yazantarifi.utils

import com.android.ddmlib.IDevice
import com.android.ddmlib.NullOutputReceiver
import com.android.tools.idea.gradle.project.sync.GradleSyncState
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiPackage
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
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
            AndroidDebugEvent.FOURCE_STOP_APPLICATION -> forceStopApplication(device)
        }
    }

    override fun killApplication(device: IDevice) {
        getAvailablePackages().forEach {
            device.kill(it.name)
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
            device.uninstallPackage(it.name)
        }
    }

    override fun forceStopApplication(device: IDevice) {
        getAvailablePackages().forEach {
            device.forceStop(it.name)
        }
    }

    override fun clearDataAndRestartApplication(device: IDevice) {

    }

    override fun clearDataApplication(device: IDevice) {

    }

    override fun removeApplicationPermissions(device: IDevice) {

    }

    override fun getAvailablePackages(): HashSet<PsiPackage> {
        val ret: HashSet<PsiPackage> = HashSet()
        val virtualFiles = FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME, JavaFileType.INSTANCE,
                GlobalSearchScope.projectScope(project)
        )

        for (vf in virtualFiles) {
            val psifile = PsiManager.getInstance(project).findFile(vf!!)
            if (psifile is PsiJavaFile) {
                JavaPsiFacade.getInstance(project).findPackage(psifile.packageName)?.let {
                    ret.add(it)
                }
            }
        }

        return ret
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
