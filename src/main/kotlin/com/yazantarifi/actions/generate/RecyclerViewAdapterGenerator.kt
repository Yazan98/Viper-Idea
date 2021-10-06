package com.yazantarifi.actions.generate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.yazantarifi.ui.FeatureNameDialog
import com.yazantarifi.utils.ApplicationUtils
import com.yazantarifi.utils.FilesUtil
import com.yazantarifi.utils.GradleManager
import java.io.File
import java.io.FileWriter

class RecyclerViewAdapterGenerator: AnAction() {

    companion object {
        private const val RECYCLER_VIEW_DEPENDENCY = "androidx.recyclerview:recyclerview:1.2.1"
        private const val RECYCLER_VIEW_KEY = "recyclerview"

        private const val HOLDERS_FILE = "holders"
        private const val ADAPTERS_FILE = "adapters"
        private const val LISTENERS_FILE = "listeners"
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            FeatureNameDialog(project) {
                if (!ApplicationUtils.isEmpty(it)) {
                    executeNewFeatureEvent(project, it, e)
                }
            }.showAndGet()
        }
    }

    private fun executeNewFeatureEvent(project: Project, featureName: String, event: AnActionEvent) {
        executeGradleDependency(project, event)
        FilesUtil.getVirtualFileByAction(event)?.let {
            if (it.exists()) {
                validateFilesStructure(it)
                generateViewHolderFile(it, featureName)
                generateAdapterFile(it, featureName, project)
                generateClickListenerFile(it, featureName, project)
            }
        }
    }

    private fun generateClickListenerFile(targetFile: VirtualFile, featureName: String, project: Project) {

    }

    private fun generateAdapterFile(targetFile: VirtualFile, featureName: String, project: Project) {

    }

    private fun generateViewHolderFile(targetFile: VirtualFile, featureName: String) {
        val viewHolderFile = File(targetFile.path, HOLDERS_FILE)
        File(viewHolderFile, featureName + "ViewHolder").apply {
            this.createNewFile()
            try {
                val writter = FileWriter(this, true)
                writter.write("package ${this.absolutePath.replace("/", ".")}\n")
                writter.write("\n")
                writter.write("\n")
                writter.write("import android.view.View\n")
                writter.write("import androidx.recyclerview.widget.RecyclerView\n")
                writter.write("\n")
                writter.write("\n")
                writter.write("class ${featureName + "ViewHolder"} constructor(view: View): RecyclerView.ViewHolder(view) {\n")
                writter.write("    val parentView: View? = view.findViewById(R.id.container)\n")
                writter.write("}\n")
                writter.flush()
                writter.close()
            } catch(ex: Exception) {
                println("Exception : ${ex.message}")
            }
        }
    }

    private fun validateFilesStructure(targetFile: VirtualFile) {
        val viewHolderFile = File(targetFile.path, HOLDERS_FILE)
        val adaptersFile = File(targetFile.path, ADAPTERS_FILE)
        val listenersFile = File(targetFile.path, LISTENERS_FILE)
        if (!viewHolderFile.exists()) {
            viewHolderFile.mkdir()
        }

        if (!adaptersFile.exists()) {
            adaptersFile.mkdir()
        }

        if (!listenersFile.exists()) {
            listenersFile.mkdir()
        }
    }

    private fun executeGradleDependency(project: Project, event: AnActionEvent) {
        GradleManager(project).apply {
            if (this.initBuildGradle()) {
                when (this.isDependencyExists(RECYCLER_VIEW_KEY)) {
                    false -> this.addDependency(RECYCLER_VIEW_DEPENDENCY, event)
                    true -> {} // Ignore The Dependency Part
                }
            }
        }
    }

}
