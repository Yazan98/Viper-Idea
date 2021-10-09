package com.yazantarifi.actions.generate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.yazantarifi.dialogs.FeatureNameDialog
import com.yazantarifi.utils.ApplicationUtils
import com.yazantarifi.utils.FilesUtil
import com.yazantarifi.utils.GradleManager
import java.io.File
import java.io.FileWriter

class RecyclerViewAdapterGenerator: AnAction() {

    companion object {
        private const val RECYCLER_VIEW_DEPENDENCY = "implementation(\"androidx.recyclerview:recyclerview:1.2.1\")"
        private const val RECYCLER_VIEW_KEY = "recyclerview"

        private const val HOLDERS_FILE = "holders"
        private const val ADAPTERS_FILE = "adapters"
        private const val LISTENERS_FILE = "listeners"
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
                generateViewHolderFile(it, featureName, project)
                generateAdapterFile(it, featureName, project)
                generateClickListenerFile(it, featureName, project)
                it.refresh(false, true)
                executeGradleDependency(project, event)
            }
        }
    }

    private fun generateClickListenerFile(targetFile: VirtualFile, featureName: String, project: Project) {
        val adaptersFile = File(targetFile.path, "adapters")
        if (!adaptersFile.exists()) {
            adaptersFile.mkdir()
        }

        val clickListenerFile = File(adaptersFile, LISTENERS_FILE)
        val packageName = ApplicationUtils.getPackageNameFromFile(clickListenerFile, project)
        File(clickListenerFile, featureName + "ClickListener" + ".kt").apply {
            this.createNewFile()
            try {
                val writter = FileWriter(this, false)
                writter.write("package ${packageName}\n")
                writter.write("\n")
                writter.write("\n")
                ApplicationUtils.addClassHeaderComment(writter, arrayListOf(
                    "This Interface is the click Listener on the Whole Item Inside Your Adapter",
                    "The Current Method Implemented On Click Listener on the Item inside onBindViewHolder Method"
                ))
                writter.write("interface ${featureName}ClickListener {\n")
                writter.write("\n")
                writter.write("     fun onItemClicked(item: Item)\n")
                writter.write("\n")
                writter.write("}\n")
                writter.write("\n")
                writter.flush()
                writter.close()
            } catch(ex: Exception) {
                println("Exception : ${ex.message}")
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
                writter.write("import ${packageName}.holders.${featureName}ViewHolder\n")
                writter.write("import ${packageName}.listeners.${featureName}ClickListener\n")
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
                writter.write("    private val itemsClickListener: ${featureName}ClickListener? = null\n")
                writter.write("): RecyclerView.Adapter<${featureName + "ViewHolder"}>() {\n")
                writter.write("\n")
                writter.write("     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ${featureName + "ViewHolder"} {\n")
                writter.write("        return ${featureName + "ViewHolder"}(LayoutInflater.from(parent.context).inflate(R.layout.items_row, parent, false))\n")
                writter.write("     }\n")
                writter.write("\n")
                writter.write("    override fun onBindViewHolder(holder: ${featureName + "ViewHolder"}, position: Int) {\n")
                writter.write("        val item = items[position]\n")
                writter.write("        holder.parentView?.setOnClickListener {\n")
                writter.write("             itemsClickListener?.onItemClicked(item)\n")
                writter.write("        }\n")
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

    private fun generateViewHolderFile(targetFile: VirtualFile, featureName: String, project: Project) {
        val adaptersFile = File(targetFile.path, "adapters")
        if (!adaptersFile.exists()) {
            adaptersFile.mkdir()
        }
        val viewHolderFile = File(adaptersFile, HOLDERS_FILE)
        val packageName = ApplicationUtils.getPackageNameFromFile(viewHolderFile, project)
        File(viewHolderFile, featureName + "ViewHolder" + ".kt").apply {
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
        val parentFileTarget = File(targetFile.path, ADAPTERS_FILE)
        if (!parentFileTarget.exists()) {
            parentFileTarget.mkdir()
        }

        val viewHolderFile = File(parentFileTarget, HOLDERS_FILE)
        val listenersFile = File(parentFileTarget, LISTENERS_FILE)
        if (!viewHolderFile.exists()) {
            viewHolderFile.mkdir()
        }

        if (!listenersFile.exists()) {
            listenersFile.mkdir()
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
