package com.yazantarifi.actions.generate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.yazantarifi.dialogs.FragmentsTypePickerDialog

class FragmentsGeneratorAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            FragmentsTypePickerDialog(project) {
                it?.forEach {

                }
            }
        }
    }

}
