package com.yazantarifi.template

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.yazantarifi.utils.ApplicationUtils
import java.io.File
import java.io.FileWriter

object FragmentGenerator {

    fun generateEmptyFragment(packageFile: File, featureName: String, project: Project, event: AnActionEvent) {
        val packageName = ApplicationUtils.getPackageNameFromFile(packageFile, event)
        val projectPackageName = ApplicationUtils.getPackageName(project)
        FileWriter(File(packageFile, featureName + "EmptyFragment.kt"), false).apply {
            write("package $packageName")
            write("\n")
            write("\n")
            write("import android.os.Bundle\n")
            write("import android.view.LayoutInflater\n")
            write("import android.view.View\n")
            write("import android.view.ViewGroup\n")
            write("import androidx.fragment.app.Fragment\n")
            write("import ${projectPackageName}.R\n")
            write("\n")
            ApplicationUtils.addClassHeaderComment(this, arrayListOf(
                "This Fragment is an Empty Fragment Generated Code",
                "Used When you want To Setup Fragment Without Specific Visible Implementation (Then Generate Basic Empty Fragment)",
                "Use getInstance Method To Create Instance of this Fragment"
            ))
            write("class ${featureName + "EmptyFragment"}: Fragment() {\n")
            write("\n")
            write("    companion object {\n")
            write("        @JvmStatic\n")
            write("        fun getInstance(args: Bundle? = null): ${featureName + "EmptyFragment"} {\n")
            write("            return ${featureName + "EmptyFragment"}().apply {\n")
            write("                this.arguments = args\n")
            write("            }\n")
            write("        }\n")
            write("    }\n")
            write("\n")
            write("    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {\n")
            write("        return inflater.inflate(R.layout.fragment_container_view, container, false)\n")
            write("    }\n")
            write("\n")
            write("    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {\n")
            write("        super.onViewCreated(view, savedInstanceState)\n")
            write("    }\n")
            write("\n")
            write("}\n")
            write("\n")
            flush()
            close()
        }
    }

    fun generateRecyclerViewFragment(packageFile: File, featureName: String, project: Project, name: String, isViewModelGenerated: Boolean = false, event: AnActionEvent) {
        val packageName = ApplicationUtils.getPackageNameFromFile(packageFile, event)
        val projectPackageName = ApplicationUtils.getPackageName(project)
        FileWriter(File(packageFile, featureName + name + "Fragment.kt"), false).apply {
            write("package $packageName")
            write("\n")
            write("\n")
            write("\n")
            write("import android.os.Bundle\n")
            write("import android.view.LayoutInflater\n")
            write("import android.view.View\n")
            write("import android.view.ViewGroup\n")
            write("import androidx.fragment.app.Fragment\n")
            write("import androidx.recyclerview.widget.LinearLayoutManager\n")
            write("import androidx.recyclerview.widget.RecyclerView\n")
            write("import androidx.fragment.app.activityViewModels\n")
            write("import ${projectPackageName}.R\n")
            write("import ${packageName}.adapters.${featureName + name}Adapter\n")
            write("\n")
            ApplicationUtils.addClassHeaderComment(this, arrayListOf(
                "Generated Fragment With Ready Implementation for RecyclerView Declaration with Adapter",
                "Use getInstance Method To Create Instance of this Fragment"
            ))
            write("class ${featureName + "${name}Fragment"}: Fragment() {\n")
            write("\n")
            if (isViewModelGenerated) {
                write("    private val viewModel: ${featureName}ViewModel by activityViewModels()\n")
            }
            write("    companion object {\n")
            write("        @JvmStatic\n")
            write("        fun getInstance(args: Bundle? = null): ${featureName + "${name}Fragment"} {\n")
            write("            return ${featureName + "${name}Fragment"}().apply {\n")
            write("                this.arguments = args\n")
            write("            }\n")
            write("        }\n")
            write("    }\n")
            write("\n")
            write("    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {\n")
            write("        return inflater.inflate(R.layout.fragment_container_view, container, false)\n")
            write("    }\n")
            write("\n")
            write("    private fun setupRecyclerView(list: RecyclerView?, items: ArrayList<*>? = null) {\n")
            write("        requireActivity().apply {\n")
            write("            list?.let {\n")
            write("                it.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)\n")
            write("                it.adapter = ${featureName + name}Adapter(items)\n")
            write("            }\n")
            write("        }\n")
            write("    }\n")
            write("\n")
            write("    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {\n")
            write("        super.onViewCreated(view, savedInstanceState)\n")
            if (isViewModelGenerated) {
                write("        subscribeListeners()\n")
            }
            write("    }\n")
            write("\n")
            if (isViewModelGenerated) {
                write("    private fun subscribeListeners() {\n")
                write("        viewModel.loadingListener.observe(viewLifecycleOwner, {\n")
                write("            when (it) {\n")
                write("                true -> showLoading()\n")
                write("                false -> hideLoading()\n")
                write("            }\n")
                write("        })\n")
                write("\n")
                write("        viewModel.dataListener.observe(viewLifecycleOwner, {\n")
                write("            it?.let {\n")
                write("                setupRecyclerView(null, it)\n")
                write("            }\n")
                write("        })\n")
                write("    }\n")
                write("\n")
            }
            write("}\n")
            write("\n")
            flush()
            close()
        }

        val adaptersPackage = File(packageFile, "adapters")
        if (!adaptersPackage.exists()) {
            adaptersPackage.mkdir()
        }

        val holdersPackage = File(adaptersPackage, "holders")
        if (!holdersPackage.exists()) {
            holdersPackage.mkdir()
        }

        generateAdapter(adaptersPackage, project, featureName, name, event)
        generateViewHolder(holdersPackage, project, featureName, name, event)
    }

    private fun generateAdapter(adapterFile: File, project: Project, featureName: String, name: String, event: AnActionEvent) {
        val packageName = ApplicationUtils.getPackageNameFromFile(adapterFile, event)
        File(adapterFile, featureName + name +"Adapter" + ".kt").apply {
            this.createNewFile()
            try {
                val writter = FileWriter(this, false)
                writter.write("package ${packageName}\n")
                writter.write("\n")
                writter.write("\n")
                writter.write("import android.view.LayoutInflater\n")
                writter.write("import android.view.ViewGroup\n")
                writter.write("import androidx.recyclerview.widget.RecyclerView\n")
                writter.write("import ${packageName}.holders.${featureName + name}ViewHolder\n")
                writter.write("import ${ApplicationUtils.getPackageName(project)}.R\n")
                writter.write("\n")
                ApplicationUtils.addClassHeaderComment(writter, arrayListOf(
                    "RecyclerView Generated Code Adapter (Single Item View)",
                    "You Can Replace Item With Your Data Class Inside Your Application",
                    "And Your Click Callback To Your Data Class Inside Your Application",
                    "This Class Will Hold All Items Inside Your List and Connect Them With ViewHolder"
                ))
                writter.write("class ${featureName + name}Adapter constructor(\n")
                writter.write("    private val items: ArrayList<Item>,\n")
                writter.write("): RecyclerView.Adapter<${featureName + name + "ViewHolder"}>() {\n")
                writter.write("\n")
                writter.write("     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ${featureName + name + "ViewHolder"} {\n")
                writter.write("        return ${featureName + name + "ViewHolder"}(LayoutInflater.from(parent.context).inflate(R.layout.items_row, parent, false))\n")
                writter.write("     }\n")
                writter.write("\n")
                writter.write("    override fun onBindViewHolder(holder: ${featureName + name + "ViewHolder"}, position: Int) {\n")
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

    private fun generateViewHolder(parentFile: File, project: Project, featureName: String, name: String, event: AnActionEvent) {
        val packageName = ApplicationUtils.getPackageNameFromFile(parentFile, event)
        File(parentFile, featureName  + name + "ViewHolder" + ".kt").apply {
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
                writter.write("class ${featureName + name + "ViewHolder"} constructor(view: View): RecyclerView.ViewHolder(view) {\n")
                writter.write("    val parentView: View? = view.findViewById(R.id.container)\n")
                writter.write("}\n")
                writter.flush()
                writter.close()
            } catch (ex: Exception) {
                println("Exception : ${ex.message}")
            }
        }
    }

}