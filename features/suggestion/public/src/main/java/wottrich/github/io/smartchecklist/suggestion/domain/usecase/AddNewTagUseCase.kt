package wottrich.github.io.smartchecklist.suggestion.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase

abstract class AddNewTagUseCase : KotlinResultUseCase<String, UseCase.Empty>()