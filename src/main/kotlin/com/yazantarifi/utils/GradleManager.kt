package com.yazantarifi.utils

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.EmptyAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.util.stream.Stream
import java.io.FileNotFoundException
import javax.swing.SwingUtilities

class GradleManager(private val project: Project) {

    companion object {
        const val DEFAULT_MODULE_NAME = "app"
        const val IMPLEMENTATION = "implementation"
        const val COMPILE = "compile"
        const val DEPENDENCIES = "dependencies"
    }

    private var buildGradle: Document? = null
    private var modules = arrayOf<Any>()
    private var projectBaseDir: VirtualFile? = null

    @Throws(FileNotFoundException::class)
    fun initBuildGradle(): Boolean {
        getModulesExist()
        val gradleVirtualFile: VirtualFile?
        gradleVirtualFile = if (modules.size > 1) {
            val isHaveAppModule: String? = modules.find { it == DEFAULT_MODULE_NAME } as String
            if (isHaveAppModule != null && isHaveAppModule != "") {
                projectBaseDir!!
                    .findChild(isHaveAppModule)!!
                    .findChild("build.gradle")
            } else {
                return false
            }
        } else {
            projectBaseDir!!
                .findChild(modules[0] as String)!!
                .findChild("build.gradle")
        }
        if (gradleVirtualFile != null) {
            buildGradle = FileDocumentManager.getInstance().getDocument(gradleVirtualFile)
        }
        return true
    }

    @Throws(FileNotFoundException::class)
    private fun getModulesExist() {
        val basePath = project.basePath
        if (ApplicationUtils.isEmpty(basePath)) {
            throw FileNotFoundException("Project base path not found.")
        }
        projectBaseDir = LocalFileSystem.getInstance().findFileByPath(basePath!!)
        if (projectBaseDir == null) {
            throw FileNotFoundException("Project base directory not found.")
        }
        val virtualSettingsGradle = projectBaseDir!!.findChild("settings.gradle")
        if (virtualSettingsGradle != null) {
            val settingsGradle = FileDocumentManager.getInstance().getDocument(virtualSettingsGradle)
            if (settingsGradle != null) {
                modules = readSettingsGradle(settingsGradle)
            }
        } else if (projectBaseDir!!.findChild("build.gradle") == null) {
            throw FileNotFoundException("Project doesn't contain any gradle file.")
        }
    }

    fun isDependencyExists(dependencyKey: String): Boolean {
        var resultBoolean = false
        val documentText = buildGradle!!.text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in documentText.indices) {
            val line = documentText[i]
            if (line.contains(dependencyKey)) {
                resultBoolean = true
                break
            } else {
                resultBoolean = false
            }
        }

        return resultBoolean
    }

    fun addDependency(repository: String, actionEvent: AnActionEvent) {
        val documentText = buildGradle!!.text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val sb = StringBuilder()
        var counter = 0
        var canSearch = false
        for (i in documentText.indices) {
            val line = documentText[i]
            if (canSearch) {
                if (line.contains("{")) {
                    counter += 1
                }
                if (line.contains("}")) {
                    if (counter > 0) {
                        counter -= 1
                    } else {
                        canSearch = false
                        sb.append("\t$repository").append("\n")
                    }
                }
            }
            if (line.contains(DEPENDENCIES)) {
                val tempLine = line.replace(DEPENDENCIES, "")
                if (tempLine.trim().equals("{", true)) {
                    canSearch = true
                } else {
                    if (!tempLine.trim().isRequiredField()) {
                        counter = -1
                        canSearch = true
                    } else {
                        if (tempLine.trim().contains("{")
                            && !tempLine.trim().contains("//")
                            && (tempLine.trim().contains(IMPLEMENTATION) || tempLine.trim().contains(COMPILE))
                        ) {
                            canSearch = true
                        }
                    }
                }
            }
            sb.append(line).append("\n")
        }
        writeToGradle(sb, actionEvent)
    }

    private fun writeToGradle(stringBuilder: StringBuilder, actionEvent: AnActionEvent) {
        val application = ApplicationManager.getApplication()
        application.invokeLater {
            application.runWriteAction { buildGradle!!.setText(stringBuilder) }
            syncProject(actionEvent)
        }
    }

    fun syncProject(actionEvent: AnActionEvent) {
        val androidSyncAction = getAction("Android.SyncProject")
        val refreshAllProjectAction = getAction("ExternalSystem.RefreshAllProjects")
        if (androidSyncAction != null && androidSyncAction !is EmptyAction) {
            androidSyncAction.actionPerformed(actionEvent)
        } else if (refreshAllProjectAction != null && refreshAllProjectAction !is EmptyAction) {
            refreshAllProjectAction.actionPerformed(actionEvent)
        } else {
            SwingUtilities.invokeLater {
                IdeaNotificationsManager(project).showNotification("Gradle Sync", "Failed To Sync Gradle")
            }
        }
    }

    private fun getAction(actionId: String): AnAction? {
        return ActionManager.getInstance().getAction(actionId)
    }

    private fun readSettingsGradle(settingsGradle: Document): Array<Any> {
        return Stream.of(*settingsGradle.text.split("'".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            .filter { s -> s.startsWith(":") }
            .map { s -> s.replace(":", "") }
            .toArray()
    }

    private fun String?.isRequiredField(): Boolean {
        return this != null && isNotEmpty() && isNotBlank()
    }
}