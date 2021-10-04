package com.yazantarifi.template.simple

object SimpleActivityBuilder {

    fun getActivityBody(
        activityName: String,
        isLayoutGenerated: Boolean = false,
        layoutName: String,
        packageName: String
    ): String {
        val layoutContent = if (isLayoutGenerated) {
            "setContentView(R.layout.$layoutName)"
        } else {
            ""
        }

        return """
            package $packageName
            
            import android.os.Bundle
            import androidx.appcompat.app.AppCompatActivity
            
            class $activityName : AppCompatActivity() {
            
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    $layoutContent
                }
                
            }
        """.trimIndent()
    }

}