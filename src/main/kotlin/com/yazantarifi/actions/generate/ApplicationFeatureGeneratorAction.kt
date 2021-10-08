package com.yazantarifi.actions.generate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.yazantarifi.models.FeatureInfo
import com.yazantarifi.ui.FeatureGeneratorDialog

class ApplicationFeatureGeneratorAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let {
            FeatureGeneratorDialog(it) {
                onFeatureInfo(it)
            }.showAndGet()
        }
    }

    private fun onFeatureInfo(info: FeatureInfo) {

    }

}
