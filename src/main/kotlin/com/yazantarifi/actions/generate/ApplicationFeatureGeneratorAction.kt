package com.yazantarifi.actions.generate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.yazantarifi.models.FeatureInfo
import com.yazantarifi.dialogs.FeatureGeneratorDialog
import com.yazantarifi.template.FragmentGenerator
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
                generateActivityClass(featurePackage, info.featureName, info.isScreenNavigationComponent, event)
            }

            if (info.isViewModelGenerated) {
                generateViewModelFile(featurePackage, info.featureName, event)
            }

            if (info.isHelperClassEnabled) {
                generateHelperFile(featurePackage, info.featureName, event)
            }

            FragmentGenerator.generateRecyclerViewFragment(featurePackage, info.featureName, project, "First", info.isViewModelGenerated, event)
            FragmentGenerator.generateRecyclerViewFragment(featurePackage, info.featureName, project, "Second", info.isViewModelGenerated, event)
            FragmentGenerator.generateRecyclerViewFragment(featurePackage, info.featureName, project, "Final", info.isViewModelGenerated, event)
            it.refresh(false, true)
        }

    }

    private fun generateHelperFile(
        featurePackage: File,
        featureName: String,
        event: AnActionEvent
    ) {
        val packageName = ApplicationUtils.getPackageNameFromFile(featurePackage, event)
        FileWriter(File(featurePackage, featureName + "Helper.kt"), false).apply {
            write("package $packageName\n")
            write("\n")
            write("object ${featureName + "Helper"} {\n")
            write("\n")
            write("}\n")
            write("\n")
            flush()
            close()
        }
    }

    private fun generateViewModelFile(featurePackage: File, featureName: String, project: AnActionEvent) {
        val packageName = ApplicationUtils.getPackageNameFromFile(featurePackage, project)
        FileWriter(File(featurePackage, featureName + "ViewModel.kt"), false).apply {
            try {
                write("package $packageName\n")
                write("\n")
                write("import androidx.lifecycle.ViewModel\n")
                write("import androidx.lifecycle.MutableLiveData\n")
                write("\n")
                ApplicationUtils.addClassHeaderComment(this, arrayListOf(
                    "This Class is the Main Point to Write Your Logic inside The Feature",
                    "Everything In ViewModels Should be Executed on Background Thread"
                ))
                write("class ${featureName + "ViewModel"} : ViewModel() {\n")
                write("\n")
                write("    val loadingListener: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }\n")
                write("    val dataListener: MutableLiveData<List<*>> by lazy { MutableLiveData<List<*>>() }\n")
                write("\n")
                write("    fun executeStarEvent(event: Int) {\n")
                write("\n")
                write("    }\n")
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

    private fun generateActivityClass(
        featurePackage: File,
        featureName: String,
        isNavigationComponents: Boolean = false,
        event: AnActionEvent
    ) {
        val packageName = ApplicationUtils.getPackageNameFromFile(featurePackage, event)
        FileWriter(File(featurePackage, featureName + "Screen.kt"), false).apply {
            try {
                write("package $packageName\n")
                write("\n")
                write("import androidx.appcompat.app.AppCompatActivity\n")
                write("import android.os.Bundle\n")
                write("import android.content.Context\n")
                write("import android.content.Intent\n")
                write("\n")
                ApplicationUtils.addClassHeaderComment(this, arrayListOf(
                    "This is the Main Start Point inside this Feature (${featureName})",
                    "Use startScreen Method to Start this Activity From any View inside Your Application",
                    "You Can Change the Type of Context to Context not FragmentActivity"
                ))
                write("class ${featureName + "Screen"} : AppCompatActivity() {\n")
                write("\n")
                write("    companion object {\n")
                write("        @JvmStatic\n")
                write("        fun startScreen(context: FragmentActivity) {\n")
                write("            Intent(context, ${featureName + "Screen"}::class.java).apply {\n")
                write("                context.startActivity(this)\n")
                write("            }\n")
                write("        }\n")
                write("    }\n")
                write("\n")
                write("    override fun onCreate(savedInstanceState: Bundle?) {\n")
                write("        super.onCreate(savedInstanceState)\n")
                write("        setContentView(R.layout_screen_name)\n")
                write("\n")
                write("\n")

                if (!isNavigationComponents) {
                    write("        supportFragmentManager.beginTransaction()\n")
                    write("            .replace(R.id.containerPager, ${featureName}FirstFragment.getInstance(null))\n")
                    write("            .addToBackStack(null)\n")
                    write("            .commit()\n")
                    write("\n")
                }

                write("\n")
                write("    }\n")
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

}
