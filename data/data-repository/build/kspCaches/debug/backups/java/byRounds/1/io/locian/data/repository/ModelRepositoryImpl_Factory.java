package io.locian.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.data.database.dao.DownloadedModelDao;
import io.locian.data.network.ModelDownloader;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ModelRepositoryImpl_Factory implements Factory<ModelRepositoryImpl> {
  private final Provider<DownloadedModelDao> downloadedModelDaoProvider;

  private final Provider<ModelDownloader> modelDownloaderProvider;

  public ModelRepositoryImpl_Factory(Provider<DownloadedModelDao> downloadedModelDaoProvider,
      Provider<ModelDownloader> modelDownloaderProvider) {
    this.downloadedModelDaoProvider = downloadedModelDaoProvider;
    this.modelDownloaderProvider = modelDownloaderProvider;
  }

  @Override
  public ModelRepositoryImpl get() {
    return newInstance(downloadedModelDaoProvider.get(), modelDownloaderProvider.get());
  }

  public static ModelRepositoryImpl_Factory create(
      Provider<DownloadedModelDao> downloadedModelDaoProvider,
      Provider<ModelDownloader> modelDownloaderProvider) {
    return new ModelRepositoryImpl_Factory(downloadedModelDaoProvider, modelDownloaderProvider);
  }

  public static ModelRepositoryImpl newInstance(DownloadedModelDao downloadedModelDao,
      ModelDownloader modelDownloader) {
    return new ModelRepositoryImpl(downloadedModelDao, modelDownloader);
  }
}
