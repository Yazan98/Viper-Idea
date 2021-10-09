package com.yazantarifi.dialogs

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.yazantarifi.utils.IdeaNotificationsManager
import org.jdesktop.swingx.JXLabel
import java.awt.BorderLayout
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class MaterialThemeDialogGenerator constructor(private val project: Project): DialogWrapper(project, true) {

    companion object {
        private val DIALOG_CONTENT = """
            <!-- Generated Code To Default Activity Theme -->
            <!-- Generated Theme Via Viper Plugin -->
            <style name="Theme.MainScreen" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
                <item name="colorPrimary">@color/purple_500</item>
                <item name="colorPrimaryVariant">@color/purple_700</item>
                <item name="colorOnPrimary">@color/white</item>
                <item name="colorSecondary">@color/teal_200</item>
                <item name="colorSecondaryVariant">@color/teal_700</item>
                <item name="colorOnSecondary">@color/black</item>
                <item name="windowNoTitle">true</item>
                <item name="windowActionBar">false</item>
                <item name="android:statusBarColor" tools:targetApi="l">?attr/colorPrimaryVariant</item>
            </style>
        """.trimIndent()
    }

    init {
        title = "Activity Theme Content"
        setOKButtonText("Copy")
        init()
    }

    override fun createCenterPanel(): JComponent? {
        return JPanel(BorderLayout()).apply {
            add(getThemeContent(), BorderLayout.CENTER)
        }
    }

    private fun getThemeContent(): JLabel {
        return JXLabel(DIALOG_CONTENT).apply {
            isLineWrap = true
        }
    }

    override fun doOKAction() {
        super.doOKAction()
        copyStringText()
        IdeaNotificationsManager(project).showNotification("Activity Theme", "Copied To Clipboard !!")
    }

    private fun copyStringText() {
        val selection = StringSelection(DIALOG_CONTENT)
        val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, selection)
    }

}