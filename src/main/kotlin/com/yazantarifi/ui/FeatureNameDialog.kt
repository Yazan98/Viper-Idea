package com.yazantarifi.ui

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent
import javax.swing.JPanel
import com.intellij.openapi.project.Project
import com.yazantarifi.utils.IdeaNotificationsManager
import java.awt.BorderLayout
import javax.swing.JTextField

class FeatureNameDialog constructor(
    project: Project,
    private val onActionExecute: (String) -> Unit
): DialogWrapper(project, true) {

    private val notificationManager: IdeaNotificationsManager by lazy { IdeaNotificationsManager(project) }
    private val textPlaceHolder: JTextField by lazy {
        JTextField("Enter The Feature Name ...")
    }

    init {
        title = "Generate New Feature"

        init()
    }

    override fun createCenterPanel(): JComponent? {
        return JPanel(BorderLayout()).apply {
            add(textPlaceHolder, BorderLayout.CENTER)
        }
    }

    override fun doCancelAction() {
        super.doCancelAction()
        notificationManager.showNotification("Generate New Feature", "Feature Canceled")
    }

    override fun doOKAction() {
        super.doOKAction()
        onActionExecute(textPlaceHolder.text.toString())
    }

}