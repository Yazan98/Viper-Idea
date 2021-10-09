package com.yazantarifi.dialogs

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JPanel

class FragmentsTypePickerDialog constructor(
    private val project: Project,
    private val listener: (ArrayList<String>) -> Unit
): DialogWrapper(project, true) {

    override fun createCenterPanel(): JComponent? {
        return JPanel(GridLayout(13, 1)).apply {

        }
    }

}