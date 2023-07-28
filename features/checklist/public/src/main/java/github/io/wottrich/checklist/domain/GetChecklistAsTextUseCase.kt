package github.io.wottrich.checklist.domain

import wottrich.github.io.coroutines.KotlinResultUseCase

abstract class GetChecklistAsTextUseCase : KotlinResultUseCase<String, String>()