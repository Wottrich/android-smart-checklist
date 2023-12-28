package wottrich.github.io.smartchecklist.newchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist

abstract class AddNewChecklistUseCase : KotlinResultUseCase<Checklist, Long?>()