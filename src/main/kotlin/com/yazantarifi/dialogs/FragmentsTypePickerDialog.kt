package com.yazantarifi.dialogs

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.yazantarifi.models.FragmentTemplate
import com.yazantarifi.utils.ApplicationUtils
import com.yazantarifi.utils.IdeaNotificationsManager
import java.awt.GridLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class FragmentsTypePickerDialog constructor(
    private val project: Project,
    private val listener: (FragmentTemplate) -> Unit
): DialogWrapper(project, true) {

    private val notificationManager: IdeaNotificationsManager by lazy { IdeaNotificationsManager(project) }
    private val textPlaceHolder: JTextField by lazy { JTextField("") }
    private val mapsCheckBox: JCheckBox by lazy { getCheckBoxInstance("Maps Fragment") }
    private val recyclerViewCheckBox: JCheckBox by lazy { getCheckBoxInstance("RecyclerView Fragment") }
    private val twoRecyclerViewCheckBox: JCheckBox by lazy { getCheckBoxInstance("2 RecyclerViews Fragment") }
    private val emptyCheckBox: JCheckBox by lazy { getCheckBoxInstance("Empty Fragment") }
    init {
        title = "Enter Feature Name"
        init()
    }

    private fun getCheckBoxInstance(title: String): JCheckBox {
        return JCheckBox(title).apply {
            this.isSelected = false
        }
    }

    override fun createCenterPanel(): JComponent? {
        return JPanel(GridLayout(6, 1)).apply {
            add(textPlaceHolder)
            add(mapsCheckBox)
            add(recyclerViewCheckBox)
            add(twoRecyclerViewCheckBox)
            add(emptyCheckBox)
        }
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return textPlaceHolder
    }

    override fun doOKAction() {
        if (ApplicationUtils.isEmpty(textPlaceHolder.text.toString().trim())) {
            notificationManager.showNotification("Invalid Input", "Feature Name Required !!")
            return
        }

        super.doOKAction()
        listener(FragmentTemplate(
            textPlaceHolder.text.toString().trim(),
            arrayListOf(
                if (mapsCheckBox.isSelected) FragmentTemplate.MAPS_FRAGMENT else "",
                if (recyclerViewCheckBox.isSelected) FragmentTemplate.RECYCLER_VIEW_FRAGMENT else "",
                if (twoRecyclerViewCheckBox.isSelected) FragmentTemplate.TWO_RECYCLER_VIEWS_FRAGMENT else "",
                if (emptyCheckBox.isSelected) FragmentTemplate.EMPTY_FRAGMENT else ""
            )
        ))
    }

}
