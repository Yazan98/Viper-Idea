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
        FilesUtil.getVirtualFileByAction(event)?.let {
            if (it.exists()) {
                generateFirebaseServiceFile(it, name, project)
                it.refresh(false, true)
                executeGradleDependency(project, event)
            }
        }
    }

    private fun generateFirebaseServiceFile(targetFile: VirtualFile, name: String, project: Project) {
        File(targetFile.path).apply {
            val filePackageName = ApplicationUtils.getPackageNameFromFile(this, project)
            val applicationPackage = ApplicationUtils.getPackageName(project)
            File(this, name + "FirebaseNotificationsService.kt").apply {
                FileWriter(this, false).apply {
                    try {
                        this.write("package $filePackageName\n")
                        this.write("\n")
                        this.write("\n")
                        this.write("import android.app.NotificationChannel\n")
                        this.write("import android.app.NotificationManager\n")
                        this.write("import android.app.PendingIntent\n")
                        this.write("import android.content.Context\n")
                        this.write("import android.content.Intent\n")
                        this.write("import android.graphics.BitmapFactory\n")
                        this.write("import android.os.Build\n")
                        this.write("import androidx.core.app.NotificationCompat\n")
                        this.write("import androidx.core.app.NotificationManagerCompat\n")
                        this.write("import com.google.firebase.messaging.FirebaseMessagingService\n")
                        this.write("import com.google.firebase.messaging.RemoteMessage\n")
                        this.write("import ${applicationPackage}.R\n")
                        this.write("import ${applicationPackage}.MainActivity\n")
                        this.write("import timber.log.Timber\n")
                        this.write("import java.util.*\n")
                        this.write("\n")
                        this.write("class ${name}FirebaseNotificationsService: FirebaseMessagingService() {\n")
                        this.write("\n")
                        this.write("    companion object {\n")
                        this.write("        private const val NOTIFICATION_CHANNEL_NAME = \"Notifications\"\n")
                        this.write("        private const val NOTIFICATION_ID = \"NotificationsChannel\"\n")
                        this.write("        private const val NOTIFICATION_DESCRIPTION = \"\"\"\n")
                        this.write("        Notification Channel is an Important Part of Notifications In Android\n")
                        this.write("        This is the Generated Description about Notification Channel\n")
                        this.write("    \"\"\"\n")
                        this.write("\n")
                        this.write("        @JvmStatic\n")
                        this.write("        fun createNotificationChannel(context: Context) {\n")
                        this.write("            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {\n")
                        this.write("                val importance = NotificationManager.IMPORTANCE_HIGH\n")
                        this.write("                val channel = NotificationChannel(NOTIFICATION_ID, NOTIFICATION_CHANNEL_NAME, importance)\n")
                        this.write("                channel.description = NOTIFICATION_DESCRIPTION\n")
                        this.write("                context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)\n")
                        this.write("                Timber.d(\"Notification Channel Created !!\")\n")
                        this.write("            }\n")
                        this.write("        }\n")
                        this.write("    }\n")
                        this.write("\n")
                        this.write("    override fun onMessageReceived(p0: RemoteMessage) {\n")
                        this.write("        super.onMessageReceived(p0)\n")
                        this.write("        p0.notification?.let {\n")
                        this.write("            showNotification(it)\n")
                        this.write("        }\n")
                        this.write("    }\n")
                        this.write("\n")
                        this.write("    private fun showNotification(notificationBody: RemoteMessage.Notification) {\n")
                        this.write("        try {\n")
                        this.write("            val intent = Intent(applicationContext, MainActivity::class.java).apply {\n")
                        this.write("                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK\n")
                        this.write("            }\n")
                        this.write("            val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)\n")
                        this.write("\n")
                        this.write("            val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_ID)\n")
                        this.write("                .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources , R.drawable.large_icon))\n")
                        this.write("                .setContentTitle(notificationBody.title ?: \"\")\n")
                        this.write("                .setContentText(notificationBody.body ?: \"\")\n")
                        this.write("                .setStyle(NotificationCompat.BigTextStyle().bigText(notificationBody.body ?: \"\"))\n")
                        this.write("                .setContentIntent(pendingIntent)\n")
                        this.write("                .setPriority(NotificationCompat.PRIORITY_HIGH)\n")
                        this.write("                .setCategory(NotificationCompat.CATEGORY_ERROR)\n")
                        this.write("\n")
                        this.write("            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {\n")
                        this.write("                builder.setSmallIcon(R.drawable.small_icon)\n")
                        this.write("            }\n")
                        this.write("\n")
                        this.write("            with(NotificationManagerCompat.from(applicationContext)) {\n")
                        this.write("                notify(getNotificationId(), builder.build())\n")
                        this.write("            }\n")
                        this.write("            Timber.d(\"DEBUGGING : Notification Shown Successfully !!\")\n")
                        this.write("        } catch (ex: Exception) {\n")
                        this.write("            Timber.e(ex)\n")
                        this.write("        }\n")
                        this.write("    }\n")
                        this.write("\n")
                        this.write("    private fun getNotificationId(): Int {\n")
                        this.write("        return Random().nextInt(1000)\n")
                        this.write("    }\n")
                        this.write("\n")
                        this.write("}\n")
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
