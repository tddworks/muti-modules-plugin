package com.tddworks.mutiplugin.ext.git

import co.touchlab.kermit.Logger
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class GitAction : AnAction("GitAction") {
    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let {
            Logger.d("GitAction triggered")
            GitExt.instance(it)
        }
    }
}