package com.yazantarifi.template

import com.intellij.openapi.project.Project
import com.yazantarifi.utils.ApplicationUtils
import java.io.File
import java.io.FileWriter

object FragmentGenerator {

    fun generateEmptyFragment(packageFile: File, featureName: String, project: Project) {
        val packageName = ApplicationUtils.getPackageNameFromFile(packageFile, project)
        val projectPackageName = ApplicationUtils.getPackageName(project)
        FileWriter(File(packageFile, featureName + "MapsFragment.kt"), false).apply {
            write("package $packageName")
            write("\n")
            write("import android.os.Bundle\n")
            write("import android.view.LayoutInflater\n")
            write("import android.view.View\n")
            write("import android.view.ViewGroup\n")
            write("import androidx.fragment.app.Fragment\n")
            write("import ${projectPackageName}.R\n")
            write("\n")
            write("class ${featureName + "EmptyFragment"}: Fragment() {\n")
            write("\n")
            write("    companion object {\n")
            write("        @JvmStatic\n")
            write("        fun getInstance(args: Bundle? = null): ${featureName + "EmptyFragment"} {\n")
            write("            return EmptyFragment().apply {\n")
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

}