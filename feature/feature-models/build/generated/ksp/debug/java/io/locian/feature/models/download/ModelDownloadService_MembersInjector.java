package io.locian.feature.models.download;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import io.locian.domain.inference.usecases.DownloadModelUseCase;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
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
public final class ModelDownloadService_MembersInjector implements MembersInjector<ModelDownloadService> {
  private final Provider<DownloadModelUseCase> downloadModelUseCaseProvider;

  private final Provider<ModelDownloadManager> downloadManagerProvider;

  public ModelDownloadService_MembersInjector(
      Provider<DownloadModelUseCase> downloadModelUseCaseProvider,
      Provider<ModelDownloadManager> downloadManagerProvider) {
    this.downloadModelUseCaseProvider = downloadModelUseCaseProvider;
    this.downloadManagerProvider = downloadManagerProvider;
  }

  public static MembersInjector<ModelDownloadService> create(
      Provider<DownloadModelUseCase> downloadModelUseCaseProvider,
      Provider<ModelDownloadManager> downloadManagerProvider) {
    return new ModelDownloadService_MembersInjector(downloadModelUseCaseProvider, downloadManagerProvider);
  }

  @Override
  public void injectMembers(ModelDownloadService instance) {
    injectDownloadModelUseCase(instance, downloadModelUseCaseProvider.get());
    injectDownloadManager(instance, downloadManagerProvider.get());
  }

  @InjectedFieldSignature("io.locian.feature.models.download.ModelDownloadService.downloadModelUseCase")
  public static void injectDownloadModelUseCase(ModelDownloadService instance,
      DownloadModelUseCase downloadModelUseCase) {
    instance.downloadModelUseCase = downloadModelUseCase;
  }

  @InjectedFieldSignature("io.locian.feature.models.download.ModelDownloadService.downloadManager")
  public static void injectDownloadManager(ModelDownloadService instance,
      ModelDownloadManager downloadManager) {
    instance.downloadManager = downloadManager;
  }
}
