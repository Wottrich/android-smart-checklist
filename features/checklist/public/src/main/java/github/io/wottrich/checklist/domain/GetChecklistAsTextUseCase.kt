package github.io.wottrich.checklist.domain

import github.io.wottrich.coroutines.KotlinResultUseCase

abstract class GetChecklistAsTextUseCase : KotlinResultUseCase<String, String>()