package com.yazantarifi.actions.adb

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.yazantarifi.models.AndroidDebugEvent
import com.yazantarifi.utils.AndroidDebugBridgeManager

class AndroidDebugEnableKeepActivitiesAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let {
            AndroidDebugBridgeManager(it).onDebugEventTriggered(AndroidDebugEvent.ENABLE_DONT_KEEP_ACTIVITIES)
        }
    }

}
