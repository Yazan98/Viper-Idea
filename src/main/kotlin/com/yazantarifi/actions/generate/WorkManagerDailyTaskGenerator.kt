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


class WorkManagerDailyTaskGenerator: AnAction() {

    companion object {
        private const val WORK_MANAGER_MULTI = "implementation(\"androidx.work:work-multiprocess:2.6.0\")"
        private const val WORK_MANAGER = "implementation(\"androidx.work:work-runtime-ktx::2.6.0\")"
        private const val WORK_MANAGER_MULTI_KEY = "work-multiprocess"
        private const val WORK_MANAGER_KEY = "work-runtime-ktx"
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            FeatureNameDialog(project, "Write Your WorkManager Name") {
                if (!ApplicationUtils.isEmpty(it)) {
                    executeNewFeatureEvent(project, it, e)
                }
            }.showAndGet()
        }
    }

    private fun executeNewFeatureEvent(project: Project, name: String, event: AnActionEvent) {
        FilesUtil.getVirtualFileByAction(event)?.let {
            if (it.exists()) {
                generateWorkManagerFile(it, name, project)
                it.refresh(false, true)
                executeGradleDependency(project, event)
            }
        }
    }

    private fun generateWorkManagerFile(targetFile: VirtualFile, name: String, project: Project) {
        File(targetFile.path).apply {
            val filePackageName = ApplicationUtils.getPackageNameFromFile(this, project)
            File(this, name + "WorkManager.kt").apply {
                FileWriter(this, false).apply {
                    try {
                        this.write("package $filePackageName\n")
                        this.write("\n")
                        this.write("import android.content.Context\n")
                        this.write("import androidx.work.*\n")
                        this.write("import java.util.*\n")
                        this.write("import java.util.concurrent.TimeUnit\n")
                        this.write("\n")
                        this.write("\n")
                        ApplicationUtils.addClassHeaderComment(this)
                        this.write("class ${name}WorkManager constructor(\n")
                        this.write("    context: Context, params: WorkerParameters\n")
                        this.write(") : Worker(context, params) {\n")
                        this.write("\n")
                        this.write("    companion object {\n")
                        this.write("        @JvmStatic\n")
                        this.write("        fun startWorker(context: Context) {\n")
                        this.write("            val constraints = Constraints.Builder()\n")
                        this.write("                .setRequiresCharging(false)\n")
                        this.write("                .setRequiredNetworkType(NetworkType.CONNECTED)\n")
                        this.write("                .setRequiresStorageNotLow(true)\n")
                        this.write("                .build()\n")
                        this.write("\n")
                        this.write("            val work = PeriodicWorkRequestBuilder<FirebaseNotificationsLogicService>(24, TimeUnit.HOURS)\n")
                        this.write("                .setConstraints(constraints)\n")
                        this.write("                .build()\n")
                        this.write("\n")
                        this.write("            val workManager = WorkManager.getInstance(context)\n")
                        this.write("            workManager.enqueueUniquePeriodicWork(UUID.randomUUID().toString(), ExistingPeriodicWorkPolicy.KEEP, work)\n")
                        this.write("        }\n")
                        this.write("    }\n")
                        this.write("\n")
                        this.write("    override fun doWork(): Result {\n")
                        this.write("        return Result.success()\n")
                        this.write("    }\n")
                        this.write("\n")
                        this.write("}\n")
                        this.write("\n")
                        this.flush()
                        this.close()
                    } catch (ex: Exception) {
                        println("Exception : ${ex.message}")
                    }
                }
            }
        }

    }

    private fun executeGradleDependency(project: Project, event: AnActionEvent) {
        GradleManager(project).apply {
            if (this.initBuildGradle()) {
                when (this.isDependencyExists(WORK_MANAGER_MULTI_KEY)) {
                    false -> {
                        this.addDependency(WORK_MANAGER_MULTI, event)
                    }
                    true -> {} // Ignore The Dependency Part
                }

                when (this.isDependencyExists(WORK_MANAGER_KEY)) {
                    false -> {
                        this.addDependency(WORK_MANAGER, event)
                        this.syncProject(event)
                    }
                    true -> {} // Ignore The Dependency Part
                }
            }
        }
    }
}