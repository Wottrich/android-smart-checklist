package github.io.wottrich.checklist.di

import github.io.wottrich.checklist.domain.DeleteChecklistUseCase
import github.io.wottrich.checklist.domain.GetChecklistAsTextUseCase
import github.io.wottrich.checklist.domain.UpdateSelectedChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.DeleteChecklistUseCaseImpl
import github.io.wottrich.checklist.domain.usecase.GetChecklistAsTextUseCaseImpl
import github.io.wottrich.checklist.domain.usecase.UpdateSelectedChecklistUseCaseImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val checklistModule = module {
    injectUseCases()
}

private fun Module.injectUseCases() {
    factory<DeleteChecklistUseCase> { DeleteChecklistUseCaseImpl(get()) }
    factory<UpdateSelectedChecklistUseCase> { UpdateSelectedChecklistUseCaseImpl(get()) }
    factory<GetChecklistAsTextUseCase> { GetChecklistAsTextUseCaseImpl(get()) }
}