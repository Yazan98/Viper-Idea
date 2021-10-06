package com.yazantarifi.utils

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiPackage
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex

import com.intellij.openapi.project.Project
import java.io.File

object ApplicationUtils {

    @JvmStatic
    fun getApplicationPackages(project: Project): HashSet<PsiPackage> {
        val ret: HashSet<PsiPackage> = HashSet()
        val virtualFiles = FileBasedIndex.getInstance().getContainingFiles(
            FileTypeIndex.NAME, JavaFileType.INSTANCE,
            GlobalSearchScope.projectScope(project)
        )

        for (vf in virtualFiles) {
            val psifile = PsiManager.getInstance(project).findFile(vf!!)
            if (psifile is PsiJavaFile) {
                JavaPsiFacade.getInstance(project).findPackage(psifile.packageName)?.let {
                    ret.add(it)
                }
            }
        }

        return ret
    }

    @JvmStatic
    fun getPackageName(project: Project): String {
        val availablePackages = getApplicationPackages(project)
        if (availablePackages.isNullOrEmpty()) {
            return ""
        }

        var packageName: String = ""
        availablePackages.forEach {
            if (isEmpty(packageName)) {
                packageName = it.qualifiedName
            }
        }

        return packageName
    }

    @JvmStatic
    fun getPackageNameFromFile(targetFile: File, project: Project): String {
        val projectPackageName = getPackageName(project)
        val packageNameSplitter = targetFile.path.replace("/", ".").split(projectPackageName)
        return if (packageNameSplitter.size > 1) projectPackageName + packageNameSplitter[1] else projectPackageName
    }

    @JvmStatic
    fun isEmpty(text: String?): Boolean {
        return text == null || text.isEmpty()
    }

}