package com.yazantarifi.actions.application

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.yazantarifi.models.AndroidDebugEvent
import com.yazantarifi.utils.AndroidDebugBridgeManager

class DeleteApplicationAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let {
            AndroidDebugBridgeManager(it).onDebugEventTriggered(AndroidDebugEvent.UN_INSTALL_APPLICATION)
        }
    }

}
