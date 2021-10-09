package com.yazantarifi.actions.generate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.yazantarifi.dialogs.MaterialThemeDialogGenerator

class ApplicationThemeGenerator: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let {
            MaterialThemeDialogGenerator(it).showAndGet()
        }
    }
}