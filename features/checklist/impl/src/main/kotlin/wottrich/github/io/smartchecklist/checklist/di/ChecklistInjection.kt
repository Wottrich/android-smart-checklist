package wottrich.github.io.smartchecklist.checklist.di

import org.koin.core.module.Module
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.checklist.domain.DeleteChecklistUseCase
import wottrich.github.io.smartchecklist.checklist.domain.GetChecklistAsTextUseCase
import wottrich.github.io.smartchecklist.checklist.domain.ObserveSelectedChecklistUuidUseCase
import wottrich.github.io.smartchecklist.checklist.domain.UpdateSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.checklist.domain.usecase.DeleteChecklistUseCaseImpl
import wottrich.github.io.smartchecklist.checklist.domain.usecase.GetChecklistAsTextUseCaseImpl
import wottrich.github.io.smartchecklist.checklist.domain.usecase.ObserveSelectedChecklistUuidUseCaseImpl
import wottrich.github.io.smartchecklist.checklist.domain.usecase.UpdateSelectedChecklistUseCaseImpl

val checklistModule = module {
    injectUseCases()
}

private fun Module.injectUseCases() {
    factory<DeleteChecklistUseCase> { DeleteChecklistUseCaseImpl(get()) }
    factory<UpdateSelectedChecklistUseCase> { UpdateSelectedChecklistUseCaseImpl(get()) }
    factory<GetChecklistAsTextUseCase> { GetChecklistAsTextUseCaseImpl(get()) }
    factory<ObserveSelectedChecklistUuidUseCase> { ObserveSelectedChecklistUuidUseCaseImpl(get()) }
}