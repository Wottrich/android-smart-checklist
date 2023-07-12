package github.io.wottrich.checklist.domain

import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.UseCase

abstract class UpdateSelectedChecklistUseCase : KotlinResultUseCase<String, UseCase.Empty>()