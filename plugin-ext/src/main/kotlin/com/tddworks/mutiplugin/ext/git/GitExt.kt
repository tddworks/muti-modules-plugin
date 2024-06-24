package com.tddworks.mutiplugin.ext.git

import com.intellij.openapi.project.Project
import git4idea.repo.GitRepositoryManager

object GitExt {
    fun instance(project: Project): GitRepositoryManager {
        return GitRepositoryManager.getInstance(project)
    }
}