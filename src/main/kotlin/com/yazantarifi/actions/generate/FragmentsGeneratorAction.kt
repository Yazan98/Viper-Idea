package com.yazantarifi.actions.generate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.yazantarifi.dialogs.FragmentsTypePickerDialog
import com.yazantarifi.models.FragmentTemplate
import com.yazantarifi.template.FragmentGenerator
import com.yazantarifi.template.MapsFragmentGenerator
import com.yazantarifi.utils.ApplicationUtils
import com.yazantarifi.utils.FilesUtil
import java.io.File

class FragmentsGeneratorAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            FragmentsTypePickerDialog(project) {
                it.also {
                    validateFragmentsSelection(it, project, e)
                }
            }.showAndGet()
        }
    }

    private fun validateFragmentsSelection(template: FragmentTemplate, project: Project, event: AnActionEvent) {
        FilesUtil.getVirtualFileByAction(event)?.let { file ->
            if (file.exists()) {
                if (!ApplicationUtils.isEmpty(template.featureName)) {
                    template.results?.forEach {
                        when (it) {
                            FragmentTemplate.EMPTY_FRAGMENT -> FragmentGenerator.generateEmptyFragment(File(file.path), template.featureName, project)
                            FragmentTemplate.MAPS_FRAGMENT -> MapsFragmentGenerator.generateMapsFragmentFile(File(file.path), template.featureName, true, project)
                            FragmentTemplate.RECYCLER_VIEW_FRAGMENT -> {}
                            FragmentTemplate.TWO_RECYCLER_VIEWS_FRAGMENT -> {}
                        }
                    }
                }
                file.refresh(false, false)
            }
        }
    }

}
