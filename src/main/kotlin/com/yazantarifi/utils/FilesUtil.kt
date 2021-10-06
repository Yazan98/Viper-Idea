package com.yazantarifi.utils

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.vfs.VirtualFile


object FilesUtil {

    fun getVirtualFileByAction(action: AnActionEvent): VirtualFile? {
        val file: DataKey<VirtualFile>? = CommonDataKeys.VIRTUAL_FILE
        return file?.getData(action.dataContext)
    }

}