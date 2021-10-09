package com.yazantarifi.template

import com.intellij.openapi.project.Project
import com.yazantarifi.utils.ApplicationUtils
import java.io.File
import java.io.FileWriter

object MapsFragmentGenerator {

    fun generateMapsFragmentFile(featurePackage: File, featureName: String, isMapAutoCompleteViewEnabled: Boolean, project: Project) {
        val packageName = ApplicationUtils.getPackageNameFromFile(featurePackage, project)
        FileWriter(File(featurePackage, featureName + "MapsFragment.kt"), false).apply {
            write("package $packageName")
            write("\n")
            write("class ${featureName + "MapsFragment"}: Fragment() {\n")
            write("\n")
            write("}\n")
            write("\n")
            flush()
            close()
        }
    }

}