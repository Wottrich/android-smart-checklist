package wottrich.github.io.smartchecklist.checklist.domain

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist

abstract class GetSelectedChecklistUseCase : KotlinResultUseCase<UseCase.None, Checklist?>()