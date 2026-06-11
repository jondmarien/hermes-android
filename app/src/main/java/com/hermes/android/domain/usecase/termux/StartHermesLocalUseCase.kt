package com.hermes.android.domain.usecase.termux

import com.hermes.android.data.local.termux.TermuxManager
import com.hermes.android.domain.model.TermuxStatus
import javax.inject.Inject

class StartHermesLocalUseCase @Inject constructor(
    private val termuxManager: TermuxManager
) {
    operator fun invoke(): TermuxStatus {
        return termuxManager.startHermes()
    }
}