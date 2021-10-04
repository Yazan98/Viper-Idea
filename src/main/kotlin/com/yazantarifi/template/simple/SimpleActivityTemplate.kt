package com.yazantarifi.template.simple

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.defaultPackageNameParameter
import com.intellij.openapi.externalSystem.model.project.ModuleData
import java.io.File
import java.net.URL

object SimpleActivityTemplate: Template {

    private val packageName = defaultPackageNameParameter
    private val generateLayout = booleanParameter {
        name = "Generate a Layout File"
        default = true
        help = "If True Will Generate Simple Activity"
    }

    private val isLauncher = booleanParameter {
        name = "Launcher Activity"
        default = false
        help = "This is the Home Activity Inside Your Application"
    }

    private val activityClassOption = stringParameter {
        name = "Activity Name"
        default = "MainScreen"
        help = "The Name of The Activity Class to Create"
        constraints = listOf(Constraint.CLASS, Constraint.UNIQUE, Constraint.NONEMPTY)
        suggest = {
            layoutToActivity(layoutName.value)
        }
    }

    private val layoutName = stringParameter {
        name = "Layout Name"
        default = "screen_main"
        help = "The Name of the Generated Layout"
        visible = { generateLayout.value }
        constraints = listOf(Constraint.LAYOUT, Constraint.UNIQUE, Constraint.NONEMPTY)
    }

    override val category: Category get() = Category.Activity
    override val constraints: Collection<TemplateConstraint> get() = emptyList()
    override val description: String get() = "Create Activity Template Simple Generated Code"
    override val formFactor: FormFactor get() = FormFactor.Mobile
    override val minCompileSdk: Int get() = 17
    override val minSdk: Int get() = 17
    override val name: String get() = "Simple Activity"
    override val recipe: Recipe
        get() = {
            simpleActivityRecipe(it as ModuleTemplateData, activityClassOption.value, isLauncher.value, generateLayout.value, layoutName.value, packageName.value)
        }
    override val revision: Int
        get() = 1
    override val uiContexts: Collection<WizardUiContext>
        get() = listOf(WizardUiContext.MenuEntry, WizardUiContext.NewProject)
    override val widgets: Collection<Widget<*>>
        get() = listOf(
            TextFieldWidget(activityClassOption),
            CheckBoxWidget(isLauncher),
            CheckBoxWidget(generateLayout),
            TextFieldWidget(layoutName),
            PackageNameWidget(packageName)
        )

    override fun thumb(): Thumb {
        return Thumb(URL("thumbs/image.png"))
    }

}