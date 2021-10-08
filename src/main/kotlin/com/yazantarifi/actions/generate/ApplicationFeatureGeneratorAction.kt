package com.yazantarifi.actions.generate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.yazantarifi.models.FeatureInfo
import com.yazantarifi.ui.FeatureGeneratorDialog
import com.yazantarifi.utils.FilesUtil
import java.io.File

class ApplicationFeatureGeneratorAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let {
            FeatureGeneratorDialog(it) {
                onFeatureInfo(it)
            }.showAndGet()
        }
    }

    private fun onFeatureInfo(info: FeatureInfo, event: AnActionEvent) {
        FilesUtil.getVirtualFileByAction(event)?.let {
            val featurePackage = File(it.path, info.packageName)
            if (!featurePackage.exists()) {
                featurePackage.mkdir()
            }

            if (!info.isFragmentsGeneratedOnly) {
                generateActivityClass(featurePackage, info.featureName)
            }

            if (info.isMapGenerated) {
                generateMapFragment(featurePackage, info.featureName, info.isMapAutoCompleteViewEnabled)
            }
        }

    }

    private fun generateActivityClass(featurePackage: File, featureName: String) {

    }

    private fun generateMapFragment(featurePackage: File, featureName: String, isMapAutoCompleteViewEnabled: Boolean) {
        val fragmentsPackage = File(featurePackage, "fragments")
        if (!fragmentsPackage.exists()) {
            fragmentsPackage.mkdir()
        }


    }

    private fun generateFragmentClass(featurePackage: File, className: String) {
        val fragmentsPackage = File(featurePackage, "fragments")
        if (!fragmentsPackage.exists()) {
            fragmentsPackage.mkdir()
        }


    }

}
