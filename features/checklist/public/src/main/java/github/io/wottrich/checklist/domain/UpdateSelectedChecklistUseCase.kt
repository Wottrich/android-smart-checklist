package github.io.wottrich.checklist.domain

import wottrich.github.io.coroutines.KotlinResultUseCase
import wottrich.github.io.coroutines.UseCase

abstract class UpdateSelectedChecklistUseCase : KotlinResultUseCase<String, UseCase.Empty>()