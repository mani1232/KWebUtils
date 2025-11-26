package cc.worldmandia.kwebutils.di

import cc.worldmandia.kwebutils.domain.model.ProjectFile
import cc.worldmandia.kwebutils.domain.repository.FileRepositoryImpl
import cc.worldmandia.kwebutils.domain.repository.IFileRepository
import cc.worldmandia.kwebutils.domain.usecase.ParseContentUseCase
import cc.worldmandia.kwebutils.presentation.feature.dashboard.DashboardViewModel
import cc.worldmandia.kwebutils.presentation.feature.editor.EditorViewModel
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