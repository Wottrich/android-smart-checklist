package wottrich.github.io.smartchecklist.checklist.domain

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase

abstract class GetChecklistAsTextUseCase : KotlinResultUseCase<String, String>()