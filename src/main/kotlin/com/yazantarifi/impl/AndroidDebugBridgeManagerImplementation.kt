package com.yazantarifi.impl

import com.android.ddmlib.IDevice
import com.intellij.psi.PsiPackage
import com.yazantarifi.models.AndroidDebugEvent

interface AndroidDebugBridgeManagerImplementation {

    fun onDebugEventTriggered(event: AndroidDebugEvent)

    fun executeEvent(event: AndroidDebugEvent, device: IDevice)

    fun toggleLayoutBounds(isEnabled: Boolean, device: IDevice)

    fun toggleDontKeepActivities(isEnabled: Boolean, device: IDevice)

    fun toggleOverdrawAreas(isEnabled: Boolean, device: IDevice)

    fun toggleShowTapsOption(isEnabled: Boolean, device: IDevice)

    fun togglePointerLocation(isEnabled: Boolean, device: IDevice)

    fun unInstallApplication(device: IDevice)

    fun clearDataAndRestartApplication(device: IDevice)

    fun killApplication(device: IDevice)

    fun forceStopApplication(device: IDevice)

    fun clearDataApplication(device: IDevice)

    fun removeApplicationPermissions(device: IDevice)

    fun isGradleSyncExecution(): Boolean

    fun getAvailablePackages(): HashSet<PsiPackage>

}
