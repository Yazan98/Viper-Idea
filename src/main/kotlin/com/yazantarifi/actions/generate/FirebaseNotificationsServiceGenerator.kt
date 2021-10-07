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

class FirebaseNotificationsServiceGenerator: AnAction() {

    companion object {
        private const val FIREBASE_MESSAGES_DEPENDENCY = "implementation(\"com.google.firebase:firebase-messaging-ktx:22.0.0\")"
        private const val FIREBASE_APP_DEPENDENCY = "implementation(\"com.google.firebase:firebase-analytics-ktx:19.0.2\")"
        private const val FIREBASE_APP_KEY = "firebase-analytics-ktx"
        private const val FIREBASE_MESSAGING_KEY = "firebase-messaging-ktx"
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            FeatureNameDialog(project, "Write Your Service Name") {
                if (!ApplicationUtils.isEmpty(it)) {
                    executeNewFeatureEvent(project, it, e)
                }
            }.showAndGet()
        }
    }

    private fun executeNewFeatureEvent(project: Project, name: String, event: AnActionEvent) {
        executeGradleDependency(project, event)
        FilesUtil.getVirtualFileByAction(event)?.let {
            if (it.exists()) {
                generateFirebaseServiceFile(it, name, project, event)
                it.refresh(false, true)
            }
        }
    }

    private fun generateFirebaseServiceFile(targetFile: VirtualFile, name: String, project: Project, event: AnActionEvent) {
        File(targetFile.path, "${name}FirebaseNotificationsService.kt").apply {
            val packageName = ApplicationUtils.getPackageNameFromFile(this, project)
            FileWriter(this, false).apply {
                try {
                    this.write("package $packageName\n")
                    this.flush()
                    this.close()
                } catch (ex: Exception) {
                    println("Exception : ${ex.message}")
                }
            }
        }
    }

    private fun executeGradleDependency(project: Project, event: AnActionEvent) {
        GradleManager(project).apply {
            if (this.initBuildGradle()) {
                when (this.isDependencyExists(FIREBASE_APP_KEY)) {
                    false -> {
                        this.addDependency(FIREBASE_APP_DEPENDENCY, event)
                    }
                    true -> {} // Ignore The Dependency Part
                }

                when (this.isDependencyExists(FIREBASE_MESSAGING_KEY)) {
                    false -> {
                        this.addDependency(FIREBASE_MESSAGES_DEPENDENCY, event)
                        this.syncProject(event)
                    }
                    true -> {} // Ignore The Dependency Part
                }
            }
        }
    }

}
