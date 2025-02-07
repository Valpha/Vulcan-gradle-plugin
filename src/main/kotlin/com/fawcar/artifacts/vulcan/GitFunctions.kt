package com.fawcar.artifacts.vulcan

import java.io.File
import java.util.*

internal fun String.execute(workDir: File? = null): String {
    ProcessBuilder(this.split(" ")).apply {
        workDir?.let { directory(it) }
    }.start().let {
        it.waitFor()
        Scanner(it.inputStream).use {
            return it.nextLine()
        }
    }
}

fun getGitCount() = "git rev-list --count HEAD".execute().trim().toInt()
fun getGitBranch() = "git branch --show-current".execute()
fun getGitCommitId() = "git rev-parse HEAD".execute()
fun getGitCommitShort() = getGitCommitId().substring(0, 8)
fun getGitCommitAuthor() = "git show -s --format=%an".execute()
