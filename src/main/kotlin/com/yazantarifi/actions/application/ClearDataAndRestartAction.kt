package com.yazantarifi.actions.application

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.yazantarifi.models.AndroidDebugEvent
import com.yazantarifi.utils.AndroidDebugBridgeManager

class ClearDataAndRestartAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let {
            AndroidDebugBridgeManager(it).onDebugEventTriggered(AndroidDebugEvent.CLEAR_DATA_AND_RESTART_APPLICATION)
        }
    }

}
