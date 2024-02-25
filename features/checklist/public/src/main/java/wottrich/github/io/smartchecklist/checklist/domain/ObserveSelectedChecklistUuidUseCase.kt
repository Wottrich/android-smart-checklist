package wottrich.github.io.smartchecklist.checklist.domain

import wottrich.github.io.smartchecklist.coroutines.FlowableUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase

abstract class ObserveSelectedChecklistUuidUseCase : FlowableUseCase<UseCase.None, String>()