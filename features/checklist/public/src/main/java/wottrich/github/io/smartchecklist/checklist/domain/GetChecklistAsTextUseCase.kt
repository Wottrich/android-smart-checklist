package wottrich.github.io.smartchecklist.checklist.domain

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase

abstract class GetChecklistAsTextUseCase : KotlinResultUseCase<UseCase.None, String>()