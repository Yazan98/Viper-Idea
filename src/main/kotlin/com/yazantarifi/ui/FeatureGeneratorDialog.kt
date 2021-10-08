package com.yazantarifi.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.yazantarifi.models.FeatureInfo
import com.yazantarifi.utils.ApplicationUtils
import com.yazantarifi.utils.IdeaNotificationsManager
import java.awt.GridLayout
import javax.swing.*

class FeatureGeneratorDialog constructor(
    private val project: Project,
    private val callback: (FeatureInfo) -> Unit
): DialogWrapper(project, true){

    private val notificationManager: IdeaNotificationsManager by lazy { IdeaNotificationsManager(project) }
    private val packageNameField: JTextField by lazy { JTextField("") }
    private val featureNameField: JTextField by lazy { JTextField("") }
    private val isViewModelGeneratedField: JCheckBox by lazy { getCheckBoxInstance("ViewModel Generation") }
    private val isNavigationComponentSupported: JCheckBox by lazy { getCheckBoxInstance("Is Navigation With NavigationComponents") }
    private val isFragmentsGeneratedOnlyField: JCheckBox by lazy { getCheckBoxInstance("Fragments Generation Only", false) }
    private val isFragmentListGeneratedField: JCheckBox by lazy { getCheckBoxInstance("Fragment RecyclerView Generation", false) }
    private val isHelperClassFieldEnabled: JCheckBox by lazy { getCheckBoxInstance("Feature Helper Class", false) }
    private val isMapAutoCompleteViewGeneratedField: JCheckBox by lazy {
        getCheckBoxInstance("Map Auto Complete View Generation").apply {
            this.isVisible = false
        }
    }

    private val isMapGeneratedField: JCheckBox by lazy {
        getCheckBoxInstance("Map Generation", false).apply {
            this.addChangeListener {
                when (this.isSelected) {
                    true -> isMapAutoCompleteViewGeneratedField.isVisible = true
                    false -> isMapAutoCompleteViewGeneratedField.isVisible = false
                }
            }
        }
    }

    init {
        title = "Generate New Feature"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        return JPanel(GridLayout(13, 1)).apply {
            add(JLabel("Enter Package Name"))
            add(packageNameField)
            add(JLabel("Enter Feature Name"))
            add(featureNameField)
            add(isViewModelGeneratedField)
            add(isNavigationComponentSupported)
            add(isFragmentsGeneratedOnlyField)
            add(isFragmentListGeneratedField)
            add(isMapGeneratedField)
            add(isMapAutoCompleteViewGeneratedField)
            add(isHelperClassFieldEnabled)
        }
    }

    override fun doOKAction() {
        if (ApplicationUtils.isEmpty(packageNameField.text.toString().trim())) {
            notificationManager.showNotification("Invalid Input", "Package Name Required !!")
            return
        }

        if (ApplicationUtils.isEmpty(featureNameField.text.toString().trim())) {
            notificationManager.showNotification("Invalid Input", "Feature Name Required !!")
            return
        }

        super.doOKAction()
        callback(FeatureInfo(
            packageNameField.text.toString().trim(),
            featureNameField.text.toString().trim(),
            isMapGeneratedField.isSelected,
            isViewModelGeneratedField.isSelected,
            isNavigationComponentSupported.isSelected,
            !isNavigationComponentSupported.isSelected,
            isFragmentsGeneratedOnlyField.isSelected,
            isFragmentListGeneratedField.isSelected,
            isMapAutoCompleteViewGeneratedField.isSelected,
            isHelperClassFieldEnabled.isSelected
        ))
    }

    private fun getCheckBoxInstance(title: String, isSelectedField: Boolean = true): JCheckBox {
        return JCheckBox(title).apply {
            isSelected = isSelectedField
        }
    }

}
