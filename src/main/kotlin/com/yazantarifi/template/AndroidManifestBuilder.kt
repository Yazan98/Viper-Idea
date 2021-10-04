package com.yazantarifi.template

import com.android.tools.idea.wizard.template.impl.activities.common.commonActivityBody
import com.android.tools.idea.wizard.template.renderIf

object AndroidManifestBuilder {

    fun getManifestContent(
        activityName: String,
        isLibrary: Boolean,
        isLauncher: Boolean,
        packageName: String
    ): String {
        val intentFilterContent = if (isLauncher) {
            """
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        """.trimIndent()
        } else {
            ""
        }

        return """
        <manifest xmlns:android="http://schemas.android.com/apk/res/android">
        
            <application>
                <activity android:name="$packageName.${activityName}">
                    ${commonActivityBody(isLauncher, isLibrary)}
                    $intentFilterContent
                </activity>
            </application>
        
        </manifest>
    """.trimIndent()
    }

}