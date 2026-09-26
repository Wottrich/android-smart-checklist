package wottrich.github.io.smartchecklist.checklist.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.checklist.data.repository.ChecklistRepository
import wottrich.github.io.smartchecklist.checklist.data.repository.ChecklistRepositoryImpl
import wottrich.github.io.smartchecklist.checklist.domain.DeleteChecklistUseCase
import wottrich.github.io.smartchecklist.checklist.domain.GetChecklistAsTextUseCase
import wottrich.github.io.smartchecklist.checklist.domain.GetSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.checklist.domain.ObserveSelectedChecklistUuidUseCase
import wottrich.github.io.smartchecklist.checklist.domain.UpdateSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.checklist.domain.usecase.DeleteChecklistUseCaseImpl
import wottrich.github.io.smartchecklist.checklist.domain.usecase.GetChecklistAsTextUseCaseImpl
import wottrich.github.io.smartchecklist.checklist.domain.usecase.GetSelectedChecklistUseCaseImpl
import wottrich.github.io.smartchecklist.checklist.domain.usecase.ObserveSelectedChecklistUuidUseCaseImpl
import wottrich.github.io.smartchecklist.checklist.domain.usecase.UpdateSelectedChecklistUseCaseImpl

val checklistModule = module {
    injectUseCases()
}

private fun Module.injectUseCases() {
    factory<DeleteChecklistUseCase> { DeleteChecklistUseCaseImpl(get()) }
    factory<UpdateSelectedChecklistUseCase> { UpdateSelectedChecklistUseCaseImpl(get()) }
    factoryOf(::GetChecklistAsTextUseCaseImpl) bind GetChecklistAsTextUseCase::class
    factory<ObserveSelectedChecklistUuidUseCase> { ObserveSelectedChecklistUuidUseCaseImpl(get()) }
    factory<ChecklistRepository> { ChecklistRepositoryImpl(get()) }
    factory<GetSelectedChecklistUseCase> {
        GetSelectedChecklistUseCaseImpl(get())
    }
}