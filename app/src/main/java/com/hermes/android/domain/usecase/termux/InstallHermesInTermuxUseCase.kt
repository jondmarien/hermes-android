package com.hermes.android.domain.usecase.termux

import com.hermes.android.data.local.termux.TermuxManager
import com.hermes.android.data.local.termux.TermuxManager.Result
import javax.inject.Inject

class InstallHermesInTermuxUseCase @Inject constructor(
    private val termuxManager: TermuxManager
) {
    operator fun invoke(): Result<String> {
        return termuxManager.installHermes()
    }
}