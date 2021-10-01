package com.yazantarifi.utils

import com.android.ddmlib.IDevice
import com.android.ddmlib.NullOutputReceiver
import com.yazantarifi.impl.AndroidDebugBridgeManagerImplementation
import com.yazantarifi.models.AndroidDebugEvent
import org.jetbrains.android.sdk.AndroidSdkUtils
import com.intellij.openapi.project.Project

class AndroidDebugBridgeManager constructor(private val project: Project): AndroidDebugBridgeManagerImplementation {

    private val notificationsManager: IdeaNotificationsManager by lazy { IdeaNotificationsManager(project) }
    companion object {
        private const val NO_CONNECTED_DEVICES_MESSAGE = "No Connected Devices ..."
        private const val ADB_TITLE = "ADB Events"
    }

    override fun onDebugEventTriggered(event: AndroidDebugEvent) {
        val connectedDevices = AndroidSdkUtils.getDebugBridge(project)?.devices
        if (connectedDevices.isNullOrEmpty()) {
            notificationsManager.showNotification(ADB_TITLE, NO_CONNECTED_DEVICES_MESSAGE)
            return
        }

        connectedDevices.forEach {
            executeEvent(event, it)
        }
    }

    override fun executeEvent(event: AndroidDebugEvent, device: IDevice) {
        when (event) {
            AndroidDebugEvent.SHOW_LAYOUT_BOUNDS -> toggleLayoutBounds(true, device)
            AndroidDebugEvent.HIDE_LAYOUT_BOUNDS -> toggleLayoutBounds(false, device)
            AndroidDebugEvent.ENABLE_DONT_KEEP_ACTIVITIES -> toggleDontKeepActivities(true, device)
            AndroidDebugEvent.DISABLE_DONT_KEEP_ACTIVITIES -> toggleDontKeepActivities(false, device)
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
