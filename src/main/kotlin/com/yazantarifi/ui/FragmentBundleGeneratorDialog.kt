package com.yazantarifi.ui

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

class FragmentBundleGeneratorDialog constructor(private val project: Project): DialogWrapper(project, true) {

    companion object {
        private val DIALOG_CONTENT = """
            companion object {
                private const val IS_ARGUMENT_ENABLED = "args.isEnabled"
                private const val IS_FEATURE_ENABLED = "args.isFeatureEnabled"
                private const val SCREEN_MODE = "args.screenMode"
        
                @JvmStatic
                fun getInstance(item: Item): Fragment {
                    return Fragment().apply {
                        this.arguments = Bundle().apply {
                            this.putBoolean(IS_ARGUMENT_ENABLED, item.isArgumentEnabled)
                            this.putBoolean(IS_FEATURE_ENABLED, item.isFeatureEnabled)
                            this.putInt(SCREEN_MODE, item.screenMode)
                        }
                    }
                }
            }
        """.trimIndent()
    }

    init {
        title = "Fragment Instance (Bundle Arguments)"
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
        IdeaNotificationsManager(project).showNotification("Fragment Instance", "Copied To Clipboard !!")
    }

    private fun copyStringText() {
        val selection = StringSelection(DIALOG_CONTENT)
        val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, selection)
    }

}