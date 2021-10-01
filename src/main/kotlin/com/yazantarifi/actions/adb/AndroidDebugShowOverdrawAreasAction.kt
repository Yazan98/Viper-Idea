package com.yazantarifi.actions.adb

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.yazantarifi.models.AndroidDebugEvent
import com.yazantarifi.utils.AndroidDebugBridgeManager

class AndroidDebugShowOverdrawAreasAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let {
            AndroidDebugBridgeManager(it).onDebugEventTriggered(AndroidDebugEvent.SHOW_OVERDRAW_AREAS)
        }
    }

}