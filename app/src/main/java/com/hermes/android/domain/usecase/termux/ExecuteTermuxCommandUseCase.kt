package com.hermes.android.domain.usecase.termux

import com.hermes.android.data.local.termux.TermuxManager
import com.hermes.android.data.local.termux.TermuxManager.Result
import javax.inject.Inject

class ExecuteTermuxCommandUseCase @Inject constructor(
    private val termuxManager: TermuxManager
) {
    operator fun invoke(command: String): Result<String> {
        return termuxManager.executeCommand(command)
    }
}