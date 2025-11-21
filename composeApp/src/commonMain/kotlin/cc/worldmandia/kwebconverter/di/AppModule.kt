package cc.worldmandia.kwebconverter.di

import cc.worldmandia.kwebconverter.domain.model.ProjectFile
import cc.worldmandia.kwebconverter.domain.repository.FileRepositoryImpl
import cc.worldmandia.kwebconverter.domain.repository.IFileRepository
import cc.worldmandia.kwebconverter.domain.usecase.ParseContentUseCase
import cc.worldmandia.kwebconverter.presentation.feature.dashboard.DashboardViewModel
import cc.worldmandia.kwebconverter.presentation.feature.editor.EditorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<IFileRepository> { FileRepositoryImpl() }
    factory { ParseContentUseCase() }

    viewModelOf(::DashboardViewModel)

    viewModel { (file: ProjectFile) ->
        EditorViewModel(file, get(), get())
    }
}