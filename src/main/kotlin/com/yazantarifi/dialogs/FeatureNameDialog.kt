package com.yazantarifi.dialogs

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent
import javax.swing.JPanel
import com.intellij.openapi.project.Project
import com.yazantarifi.utils.IdeaNotificationsManager
import java.awt.BorderLayout
import java.awt.Color
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.JTextField

class FeatureNameDialog constructor(
    project: Project,
    private val featureNamePlaceHolder: String,
    private val onActionExecute: (String) -> Unit
): DialogWrapper(project, true) {

    private val notificationManager: IdeaNotificationsManager by lazy { IdeaNotificationsManager(project) }
    private val textPlaceHolder: JTextField by lazy {
        JTextField(featureNamePlaceHolder).apply {
            foreground = Color.WHITE
            addFocusListener(object: FocusListener {
                override fun focusLost(e: FocusEvent?) {
                    if (text.equals(featureNamePlaceHolder)) {
                        text = "";
                        foreground = Color.WHITE
                    }
                }

                override fun focusGained(e: FocusEvent?) {
                    if (text.isEmpty()) {
                        foreground = Color.WHITE
                        text = featureNamePlaceHolder
                    }
                }
            })
        }
    }

    init {
        title = "Generate New Feature"
        init()
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return textPlaceHolder
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