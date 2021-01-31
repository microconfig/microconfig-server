package io.microconfig.server.git

import io.microconfig.server.common.logger
import io.microconfig.server.git.exceptions.RefNotFound
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Ref
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils.deleteRecursively
import java.io.File
import java.time.Instant
import java.time.Instant.now
import java.util.concurrent.ConcurrentHashMap

@Service
class GitServiceImpl(private val config: GitConfig) : GitService {
    private val log = logger()
    private val checkouts = ConcurrentHashMap<String, GitCheckout>()

    final override fun checkoutRef(ref: String?): File {
        val r = ref ?: config.defaultBranch
        val checkout = checkouts.computeIfAbsent(r) { createCheckout(r) }
        pull(checkout)
        return checkout.dir
    }

    private fun createCheckout(name: String): GitCheckout {
        val refDir = config.dir().resolve(name)
        val gitDir = refDir.resolve(".git")
        val git = if (gitDir.exists()) useLocal(gitDir) else clone(refDir)
        return GitCheckout(name = name, dir = refDir, git = git)
            .apply { checkout(this) }
    }

    private fun useLocal(gitDir: File): Git {
        val git = Git(FileRepository(gitDir))
        git.fetch()
            .setCredentialsProvider(config.credentialsProvider())
            .call()
        return git
    }

    private fun clone(dir: File): Git {
        log.info("Cloning to ${dir.absolutePath}")
        return Git.cloneRepository()
            .setCredentialsProvider(config.credentialsProvider())
            .setURI(config.remoteUrl)
            .setDirectory(dir)
            .call()
    }

    private fun checkout(checkout: GitCheckout) {
        val (name, git) = checkout
        val r = git.repository.refDatabase.refs.firstOrNull { it.name.endsWith(name) }
        checkout.ref = r

        if (r == null) {
            deleteRecursively(checkout.dir)
            throw RefNotFound(name)
        }

        // second case for previous tag checkout
        if (git.repository.branch != name && git.repository.branch != r.objectId.name) {
            git.checkout()
                .setCreateBranch(true)
                .setStartPoint(r.name)
                .setName(name)
                .call()
        }

        checkout.nextPull = now().plusSeconds(config.pullDelay)
    }

    private fun pull(checkout: GitCheckout) {
        synchronized(checkout) {
            if (checkout.timeToCheckout()) pullRef(checkout)
        }
    }

    private fun pullRef(checkout: GitCheckout) {
        if (checkout.isTag()) return
        log.debug("Pulling ref {}", checkout.name)
        checkout.git.pull()
            .setCredentialsProvider(config.credentialsProvider())
            .call()
        checkout.nextPull = now().plusSeconds(config.pullDelay)
    }

    data class GitCheckout(
        val name: String,
        var git: Git,
        val dir: File,
        var ref: Ref? = null,
        var nextPull: Instant = now()
    ) {
        fun timeToCheckout(): Boolean {
            return now().isAfter(nextPull)
        }

        fun isTag(): Boolean = ref!!.name.startsWith("refs/tags/")
    }
}