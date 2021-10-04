package com.yazantarifi.template.simple

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.PackageName
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.ThemeData
import com.android.tools.idea.wizard.template.impl.activities.common.addAllKotlinDependencies
import com.android.tools.idea.wizard.template.impl.activities.common.androidManifestXml
import com.android.tools.idea.wizard.template.impl.activities.common.generateSimpleLayout

fun RecipeExecutor.simpleActivityRecipe(
    moduleData: ModuleTemplateData,
    activityClass: String,
    isLauncher: Boolean = false,
    generateLayout: Boolean = false,
    layoutName: String,
    packageName: String
) {
    val (projectData, srcOut, resOut, manifestOut) = moduleData
    addAllKotlinDependencies(moduleData)
    mergeXml(
        source = androidManifestXml(activityClass = activityClass, isLibraryProject = moduleData.isLibrary,
            isLauncher = isLauncher, packageName = packageName, generateActivityTitle = false,
            hasApplicationTheme = false, hasNoActionBar = false, requireTheme = false, isNewModule = false,
            hasNoActionBarTheme = ThemeData("Theme.Application", false), mainTheme = ThemeData("Theme.Application", false)),
        to = manifestOut.resolve("AndroidManifest.xml")
    )

    if (generateLayout) {
        generateSimpleLayout(moduleData, activityClass, layoutName, false, PackageName(), "menu", true)
    }

    addDependency("androidx.appcompat:appcompat:1.0.2")
    save(source = SimpleActivityBuilder.getActivityBody(activityClass, generateLayout, layoutName, packageName), to = srcOut.resolve("${activityClass}.kt"))
    open(srcOut.resolve("${activityClass}.kt"))
}