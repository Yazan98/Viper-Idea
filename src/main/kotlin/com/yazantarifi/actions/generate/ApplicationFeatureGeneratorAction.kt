package com.yazantarifi.actions.generate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.yazantarifi.models.FeatureInfo
import com.yazantarifi.ui.FeatureGeneratorDialog
import com.yazantarifi.utils.ApplicationUtils
import com.yazantarifi.utils.FilesUtil
import java.io.File
import java.io.FileWriter

class ApplicationFeatureGeneratorAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            FeatureGeneratorDialog(project) {
                onFeatureInfo(it, e, project)
            }.showAndGet()
        }
    }

    private fun onFeatureInfo(info: FeatureInfo, event: AnActionEvent, project: Project) {
        FilesUtil.getVirtualFileByAction(event)?.let {
            val featurePackage = File(it.path, info.packageName)
            if (!featurePackage.exists()) {
                featurePackage.mkdir()
            }

            if (!info.isFragmentsGeneratedOnly) {
                generateActivityClass(featurePackage, info.featureName, project)
            }

            if (info.isMapGenerated) {
                generateMapFragment(featurePackage, info.featureName, info.isMapAutoCompleteViewEnabled)
            }

            if (info.isViewModelGenerated) {
                generateViewModelFile(featurePackage, info.featureName, project)
            }


            generateFragmentClass(featurePackage, info.featureName + "First", project)
            generateFragmentClass(featurePackage, info.featureName + "Second", project)
            generateFragmentClass(featurePackage, info.featureName + "Final", project)
            it.refresh(false, true)
        }

    }

    private fun generateViewModelFile(featurePackage: File, featureName: String, project: Project) {
        val packageName = ApplicationUtils.getPackageNameFromFile(featurePackage, project)
        FileWriter(File(featurePackage, featureName + "ViewModel.kt"), false).apply {
            try {
                write("package $packageName\n")
                write("\n")
                write("import androidx.lifecycle.ViewModel\n")
                write("\n")
                ApplicationUtils.addClassHeaderComment(this)
                write("class ${featureName + "ViewModel"} : ViewModel() {\n")
                write("\n")
                write("}\n")
                write("\n")
                flush()
                close()
                println("Activity Generated !! (${featureName + "Screen"})")
            } catch (e: Exception) {
                println("Failed to generate Activity Class : ${e.message}")
            }
        }
    }

    private fun generateActivityClass(featurePackage: File, featureName: String, project: Project) {
        val packageName = ApplicationUtils.getPackageNameFromFile(featurePackage, project)
        FileWriter(File(featurePackage, featureName + "Screen.kt"), false).apply {
            try {
                write("package $packageName\n")
                write("\n")
                write("import androidx.appcompat.app.AppCompatActivity\n")
                write("\n")
                ApplicationUtils.addClassHeaderComment(this)
                write("class ${featureName + "Screen"} : AppCompatActivity() {\n")
                write("\n")
                write("}\n")
                write("\n")
                flush()
                close()
                println("Activity Generated !! (${featureName + "Screen"})")
            } catch (e: Exception) {
                println("Failed to generate Activity Class : ${e.message}")
            }
        }
    }

    private fun generateMapFragment(featurePackage: File, featureName: String, isMapAutoCompleteViewEnabled: Boolean) {
        val fragmentsPackage = File(featurePackage, "fragments")
        if (!fragmentsPackage.exists()) {
            fragmentsPackage.mkdir()
        }


    }

    private fun generateFragmentClass(featurePackage: File, className: String, project: Project) {
        val fragmentsPackage = File(featurePackage, "fragments")
        if (!fragmentsPackage.exists()) {
            fragmentsPackage.mkdir()
        }

        val packageName = ApplicationUtils.getPackageNameFromFile(fragmentsPackage, project)
        FileWriter(File(fragmentsPackage, className + "Fragment.kt"), false).apply {
            try {
                write("package $packageName\n")
                write("\n")
                write("import androidx.appcompat.app.Fragment\n")
                write("\n")
                ApplicationUtils.addClassHeaderComment(this)
                write("class ${className + "Fragment"} : Fragment() {\n")
                write("\n")
                write("}\n")
                write("\n")
                flush()
                close()
                println("Fragment Generated !! (${className + "Screen"})")
            } catch (e: Exception) {
                println("Failed to generate Fragment Class : ${e.message}")
            }
        }
    }

}
