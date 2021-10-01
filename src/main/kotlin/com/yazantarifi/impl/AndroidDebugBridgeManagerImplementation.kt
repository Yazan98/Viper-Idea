package com.yazantarifi.impl

import com.android.ddmlib.IDevice
import com.yazantarifi.models.AndroidDebugEvent

interface AndroidDebugBridgeManagerImplementation {

    fun onDebugEventTriggered(event: AndroidDebugEvent)

    fun executeEvent(event: AndroidDebugEvent, device: IDevice)

    fun toggleLayoutBounds(isEnabled: Boolean, device: IDevice)

    fun toggleDontKeepActivities(isEnabled: Boolean, device: IDevice)

}
