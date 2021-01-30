package io.microconfig.server.git

import io.microconfig.server.common.logger
import io.microconfig.server.git.exceptions.BranchNotFoundException
import io.microconfig.server.git.exceptions.TagNotFoundException
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.springframework.stereotype.Service
import java.io.File
import java.time.Instant
import java.time.Instant.now

@Service
class GitServiceImpl(private val config: GitConfig) : GitService {
    private val log = logger()
    private val git: Git
    private val configDir: File
    private val pulls = HashMap<String, Instant>()

    init {
        git = build(config)
        configDir = git.repository.directory.parentFile
        checkoutDefault()
    }

    private final fun build(config: GitConfig): Git {
        val localDir = File(config.workingDir, config.defaultBranch)
        return if (localDir.exists()) useLocal(localDir) else cloneRemote(config, localDir, config.defaultBranch)
    }

    @Synchronized
    final override fun checkoutDefault(): File {
        return checkoutBranch(config.defaultBranch)
    }

    @Synchronized
    override fun checkoutBranch(branch: String): File {
        return try {
            checkout(branch)
            pullBranch(branch)
            configDir
        } catch (e: RefNotFoundException) {
            throw BranchNotFoundException(branch)
        }
    }

    private fun checkout(branch: String) {
        git.fetch().setCredentialsProvider(config.credentialsProvider()).call()
        if (git.repository.findRef(branch) == null) {
            createBranch(branch)
        }
        git.checkout().setName(branch).call()
    }

    private fun createBranch(branch: String) {
        log.debug("Creating and pulling branch {}", branch)
        git.branchCreate()
            .setName(branch)
            .setStartPoint("origin/$branch")
            .call()
    }

    private fun pullBranch(branch: String) {
        val now: Instant = Instant.now()
        val nextPull = pulls.computeIfAbsent(branch) { now.minusSeconds(1) }
        if (now.isAfter(nextPull)) {
            log.debug("Pulling branch {}", branch)
            git.pull().setCredentialsProvider(config.credentialsProvider()).call()
            pulls[branch] = now.plusSeconds(10)
        }
    }

    override fun checkoutTag(tag: String): File {
        return try {
            val expected = "refs/tags/$tag"
            git.fetch().call()
            val foundTag = git.tagList().call().asSequence()
                .onEach { println(it.name) }
                .firstOrNull { it.name == expected }
                ?: throw TagNotFoundException(tag)

            git.checkout().setName(foundTag.name).call()
            configDir
        } catch (e: GitAPIException) {
            throw TagNotFoundException(tag)
        }
    }

    private fun useLocal(localDir: File): Git {
        return Git(FileRepository(File(localDir, "/.git")))
    }

    @Throws(GitAPIException::class)
    private fun cloneRemote(config: GitConfig, localDir: File, branch: String): Git {
        log.info("Cloning $branch to ${localDir.absolutePath}")
        return Git.cloneRepository()
            .setCredentialsProvider(config.credentialsProvider())
            .setURI(config.remoteUrl)
            .setDirectory(localDir)
            .setBranch(branch)
            .call()
    }

    data class GitCheckouts(val branch: String, val pull: Instant = now().minusSeconds(1), val dir: File)
}