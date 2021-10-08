package com.yazantarifi.actions.generate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.yazantarifi.ui.FragmentBundleGeneratorDialog

class FragmentBundleGeneratorAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            FragmentBundleGeneratorDialog(project).showAndGet()
        }
    }

}