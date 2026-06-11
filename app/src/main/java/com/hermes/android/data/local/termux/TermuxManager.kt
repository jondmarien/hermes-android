package com.hermes.android.data.local.termux

import android.content.Context
import com.hermes.android.data.local.preferences.HermesPreferences
import com.hermes.android.domain.model.TermuxStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class TermuxManager(
    private val context: Context,
    private val preferences: HermesPreferences
) {

    private val processScope = CoroutineScope(Dispatchers.IO)
    private val currentProcess = AtomicReference<Process?>(null)
    private val outputChannel = Channel<String>(Channel.UNLIMITED)
    private var processJob: Job? = null

    val termuxPath: String
        get() = preferences.termuxPath ?: "/data/data/com.termux/files/usr/bin/bash"

    suspend fun startHermes(): TermuxStatus {
        return try {
            val process = startProcess("hermes gateway --api-server-enabled")
            currentProcess.set(process)
            
            // Read output for a bit to confirm startup
            val output = withTimeoutOrNull(5000) {
                readProcessOutput(process)
            }
            
            val isRunning = process.isAlive
            val hermesInstalled = checkHermesInstalled()
            
            TermuxStatus(
                isInstalled = isTermuxInstalled(),
                isRunning = isRunning,
                hermesInstalled = hermesInstalled,
                version = getHermesVersion(),
                lastOutput = output?.take(500),
                error = if (!isRunning) "Process died" else null
            )
        } catch (e: Exception) {
            TermuxStatus(
                isInstalled = isTermuxInstalled(),
                isRunning = false,
                hermesInstalled = false,
                error = e.message
            )
        }
    }

    suspend fun stopHermes(): TermuxStatus {
        currentProcess.getAndSet(null)?.destroy()
        processJob?.cancel()
        processJob = null
        return getStatus()
    }

    suspend fun getStatus(): TermuxStatus {
        val installed = isTermuxInstalled()
        val running = currentProcess.get()?.isAlive == true
        val hermesInstalled = if (installed) checkHermesInstalled() else false
        
        return TermuxStatus(
            isInstalled = installed,
            isRunning = running,
            hermesInstalled = hermesInstalled,
            version = if (hermesInstalled) getHermesVersion() else null,
            error = if (!installed) "Termux not installed" else null
        )
    }

    suspend fun executeCommand(command: String): Result<String> {
        return try {
            val process = startProcess(command)
            val output = readProcessOutput(process)
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                Result.Success(output)
            } else {
                Result.Error(Exception("Exit code: $exitCode\n$output"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun sendCommand(command: String, sessionId: Long): Result<String> {
        // For interactive chat, we use hermes chat -q
        return executeCommand(command)
    }

    suspend fun installHermes(): Result<String> {
        val commands = listOf(
            "pkg update -y && pkg install -y python git",
            "pip install --upgrade pip",
            "pip install hermes-agent"
        )
        
        var fullOutput = ""
        for (cmd in commands) {
            val result = executeCommand(cmd)
            result.onSuccess { fullOutput += "\n$it" }
            result.onFailure { return Result.Error(it) }
        }
        
        return Result.Success(fullOutput)
    }

    private fun startProcess(command: String): Process {
        val fullCommand = "$termuxPath -c \"$command\""
        return Runtime.getRuntime().exec(fullCommand)
    }

    private fun readProcessOutput(process: Process): String {
        val output = StringBuilder()
        BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            reader.forEachLine { line ->
                output.append(line).append("\n")
                outputChannel.trySend(line)
            }
        }
        return output.toString()
    }

    private fun isTermuxInstalled(): Boolean {
        return try {
            Runtime.getRuntime().exec("$termuxPath -c \"echo test\"").waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    private fun checkHermesInstalled(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("$termuxPath -c \"hermes --version\"")
            process.waitFor(5, TimeUnit.SECONDS) && process.exitValue() == 0
        } catch (e: Exception) {
            false
        }
    }

    private fun getHermesVersion(): String? {
        return try {
            val process = Runtime.getRuntime().exec("$termuxPath -c \"hermes --version\"")
            val output = BufferedReader(InputStreamReader(process.inputStream)).readText()
            process.waitFor(5, TimeUnit.SECONDS)
            output.trim().takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            null
        }
    }

    sealed class Result<out T> {
        data class Success<T>(val value: T) : Result<T>()
        data class Error(val exception: Throwable) : Result<Nothing>()
    }
}