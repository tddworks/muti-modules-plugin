package com.tddworks.mutiplugin.ext.git

import co.touchlab.kermit.Logger
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepositoryManager
import kotlinx.coroutines.CoroutineScope

object GitExt {
    fun instance(project: Project): GitRepositoryManager {

        val instance = GitRepositoryManager.getInstance(project)
        Logger.d("GitExt.instance: $instance")
        return instance
    }

}

@Service(Service.Level.PROJECT)
class CopilotCoroutineScope(private val coroutineScope: CoroutineScope) {
    companion object {
        fun getScope(project: Project): CoroutineScope {
            val service: CopilotCoroutineScope = project.service()
            return service.coroutineScope
        }
    }
}

