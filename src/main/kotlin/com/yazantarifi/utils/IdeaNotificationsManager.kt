package com.yazantarifi.utils

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.yazantarifi.impl.IdeaNotificationsManagerImplementation
import com.intellij.openapi.project.Project

class IdeaNotificationsManager constructor(private val project: Project): IdeaNotificationsManagerImplementation {

    companion object {
        private const val DISPLAY_ID = "ViperNotification"
    }

    override fun showNotification(title: String, message: String) {
        NotificationGroup(DISPLAY_ID, NotificationDisplayType.BALLOON, true).apply {
            this.createNotification(title, message, NotificationType.WARNING, null).notify(project)
        }
    }

}
