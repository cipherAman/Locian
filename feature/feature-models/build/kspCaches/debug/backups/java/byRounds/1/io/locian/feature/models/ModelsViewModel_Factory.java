package io.locian.feature.models;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.data.datastore.AppPreferences;
import io.locian.domain.inference.repository.ModelRepository;
import io.locian.domain.inference.usecases.DownloadModelUseCase;
import io.locian.domain.inference.usecases.LoadModelUseCase;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class ModelsViewModel_Factory implements Factory<ModelsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<ModelRepository> modelRepositoryProvider;

  private final Provider<LoadModelUseCase> loadModelUseCaseProvider;

  private final Provider<DownloadModelUseCase> downloadModelUseCaseProvider;

  private final Provider<AppPreferences> appPreferencesProvider;

  public ModelsViewModel_Factory(Provider<Context> contextProvider,
      Provider<ModelRepository> modelRepositoryProvider,
      Provider<LoadModelUseCase> loadModelUseCaseProvider,
      Provider<DownloadModelUseCase> downloadModelUseCaseProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    this.contextProvider = contextProvider;
    this.modelRepositoryProvider = modelRepositoryProvider;
    this.loadModelUseCaseProvider = loadModelUseCaseProvider;
    this.downloadModelUseCaseProvider = downloadModelUseCaseProvider;
    this.appPreferencesProvider = appPreferencesProvider;
  }

  @Override
  public ModelsViewModel get() {
    return newInstance(contextProvider.get(), modelRepositoryProvider.get(), loadModelUseCaseProvider.get(), downloadModelUseCaseProvider.get(), appPreferencesProvider.get());
  }

  public static ModelsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<ModelRepository> modelRepositoryProvider,
      Provider<LoadModelUseCase> loadModelUseCaseProvider,
      Provider<DownloadModelUseCase> downloadModelUseCaseProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    return new ModelsViewModel_Factory(contextProvider, modelRepositoryProvider, loadModelUseCaseProvider, downloadModelUseCaseProvider, appPreferencesProvider);
  }

  public static ModelsViewModel newInstance(Context context, ModelRepository modelRepository,
      LoadModelUseCase loadModelUseCase, DownloadModelUseCase downloadModelUseCase,
      AppPreferences appPreferences) {
    return new ModelsViewModel(context, modelRepository, loadModelUseCase, downloadModelUseCase, appPreferences);
  }
}
