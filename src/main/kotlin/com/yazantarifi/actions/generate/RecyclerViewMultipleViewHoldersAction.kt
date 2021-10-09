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

class RecyclerViewMultipleViewHoldersAction: AnAction() {

    companion object {
        private const val RECYCLER_VIEW_DEPENDENCY = "implementation(\"androidx.recyclerview:recyclerview:1.2.1\")"
        private const val RECYCLER_VIEW_KEY = "recyclerview"

        private const val HOLDERS_FILE = "holders"
        private const val ADAPTERS_FILE = "adapters"
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            FeatureNameDialog(project, "Write RecyclerView Adapter Name") {
                if (!ApplicationUtils.isEmpty(it)) {
                    executeNewFeatureEvent(project, it, e)
                }
            }.showAndGet()
        }
    }

    private fun executeNewFeatureEvent(project: Project, featureName: String, event: AnActionEvent) {
        FilesUtil.getVirtualFileByAction(event)?.let {
            if (it.exists()) {
                validateFilesStructure(it)
                generateViewHolderFile(it, featureName, project, "First")
                generateViewHolderFile(it, featureName, project, "Second")
                generateViewHolderFile(it, featureName, project, "Final")
                generateAdapterFile(it, featureName, project)
                it.refresh(false, true)
                executeGradleDependency(project, event)
            }
        }
    }

    private fun generateAdapterFile(targetFile: VirtualFile, featureName: String, project: Project) {
        val adapterFile = File(targetFile.path, ADAPTERS_FILE)
        val packageName = ApplicationUtils.getPackageNameFromFile(adapterFile, project)
        File(adapterFile, featureName + "Adapter" + ".kt").apply {
            this.createNewFile()
            try {
                val writter = FileWriter(this, false)
                writter.write("package ${packageName}\n")
                writter.write("\n")
                writter.write("\n")
                writter.write("import android.view.LayoutInflater\n")
                writter.write("import android.view.ViewGroup\n")
                writter.write("import androidx.recyclerview.widget.RecyclerView\n")
                writter.write("import ${packageName}.holders.${featureName}ViewHolderFirst\n")
                writter.write("import ${packageName}.holders.${featureName}ViewHolderSecond\n")
                writter.write("import ${packageName}.holders.${featureName}ViewHolderFinal\n")
                writter.write("import ${ApplicationUtils.getPackageName(project)}.R\n")
                writter.write("\n")
                ApplicationUtils.addClassHeaderComment(writter, arrayListOf(
                    "RecyclerView Generated Code Adapter (Single Item View)",
                    "You Can Replace Item With Your Data Class Inside Your Application",
                    "And Your Click Callback To Your Data Class Inside Your Application",
                    "This Class Will Hold All Items Inside Your List and Connect Them With ViewHolder"
                ))
                writter.write("class ${featureName}Adapter constructor(\n")
                writter.write("    private val items: ArrayList<Item>,\n")
                writter.write("): RecyclerView.Adapter<RecyclerView.ViewHolder>() {\n")
                writter.write("\n")
                writter.write("    companion object {\n")
                writter.write("        const val VIEW_TYPE_FIRST = 1\n")
                writter.write("        const val VIEW_TYPE_SECOND = 2\n")
                writter.write("        const val VIEW_TYPE_FINAL = 3\n")
                writter.write("    }\n")
                writter.write("\n")
                writter.write("    override fun getItemViewType(position: Int): Int {\n")
                writter.write("        return try {\n")
                writter.write("            items[position].viewType\n")
                writter.write("        } catch (ex: Exception) {\n")
                writter.write("            super.getItemViewType(position)\n")
                writter.write("        }\n")
                writter.write("    }\n")
                writter.write("\n")
                writter.write("     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {\n")
                writter.write("        return when (viewType) {\n")
                writter.write("            VIEW_TYPE_FIRST -> ${featureName}ViewHolderFirst(LayoutInflater.from(parent.context).inflate(R.layout.items_row, parent, false))\n")
                writter.write("            VIEW_TYPE_SECOND -> ${featureName}ViewHolderSecond(LayoutInflater.from(parent.context).inflate(R.layout.items_row_2, parent, false))\n")
                writter.write("            VIEW_TYPE_FINAL -> ${featureName}ViewHolderFinal(LayoutInflater.from(parent.context).inflate(R.layout.items_row_3, parent, false))\n")
                writter.write("            else -> ${featureName}ViewHolderFirst(LayoutInflater.from(parent.context).inflate(R.layout.items_row, parent, false))\n")
                writter.write("        }\n")
                writter.write("     }\n")
                writter.write("\n")
                writter.write("    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {\n")
                writter.write("        val item = items[position]\n")
                writter.write("    }\n")
                writter.write("\n")
                writter.write("    override fun getItemCount(): Int {\n")
                writter.write("        return items.size\n")
                writter.write("    }\n")
                writter.write("}\n")
                writter.flush()
                writter.close()
            } catch(ex: Exception) {
                println("Exception : ${ex.message}")
            }
        }
    }

    private fun generateViewHolderFile(targetFile: VirtualFile, featureName: String, project: Project, order: String) {
        val adaptersFile = File(targetFile.path, "adapters")
        if (!adaptersFile.exists()) {
            adaptersFile.mkdir()
        }
        val viewHolderFile = File(adaptersFile, HOLDERS_FILE)
        val packageName = ApplicationUtils.getPackageNameFromFile(viewHolderFile, project)
        File(viewHolderFile, featureName + "ViewHolder" + order + ".kt").apply {
            this.createNewFile()
            try {
                val writter = FileWriter(this, false)
                writter.write("package ${packageName}\n")
                writter.write("\n")
                writter.write("\n")
                writter.write("import android.view.View\n")
                writter.write("import androidx.recyclerview.widget.RecyclerView\n")
                writter.write("import ${ApplicationUtils.getPackageName(project)}.R\n")
                writter.write("\n")
                writter.write("\n")
                ApplicationUtils.addClassHeaderComment(writter, arrayListOf(
                    "RecyclerView Adapter ViewHolder Generated",
                    "This File Will Hold All Views Inside Your Adapter",
                    "You Can Bind All Views Inside onBindViewHolder In Adapter"
                ))
                writter.write("class ${featureName + "ViewHolder" + order} constructor(view: View): RecyclerView.ViewHolder(view) {\n")
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
        val parentFileTarget = File(targetFile.path, ADAPTERS_FILE)
        if (!parentFileTarget.exists()) {
            parentFileTarget.mkdir()
        }

        val viewHolderFile = File(parentFileTarget, HOLDERS_FILE)
        if (!viewHolderFile.exists()) {
            viewHolderFile.mkdir()
        }
    }

    private fun executeGradleDependency(project: Project, event: AnActionEvent) {
        GradleManager(project).apply {
            if (this.initBuildGradle()) {
                when (this.isDependencyExists(RECYCLER_VIEW_KEY)) {
                    false -> {
                        this.addDependency(RECYCLER_VIEW_DEPENDENCY, event)
                        this.syncProject(event)
                    }
                    true -> {} // Ignore The Dependency Part
                }
            }
        }
    }

}
