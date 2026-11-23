package io.locian.data.repository.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.data.network.ModelDownloader;
import javax.annotation.processing.Generated;

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
public final class DataModule_ProvideModelDownloaderFactory implements Factory<ModelDownloader> {
  @Override
  public ModelDownloader get() {
    return provideModelDownloader();
  }

  public static DataModule_ProvideModelDownloaderFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ModelDownloader provideModelDownloader() {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideModelDownloader());
  }

  private static final class InstanceHolder {
    private static final DataModule_ProvideModelDownloaderFactory INSTANCE = new DataModule_ProvideModelDownloaderFactory();
  }
}
