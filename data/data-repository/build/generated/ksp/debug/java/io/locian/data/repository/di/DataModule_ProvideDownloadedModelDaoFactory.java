package io.locian.data.repository.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.data.database.LocianDatabase;
import io.locian.data.database.dao.DownloadedModelDao;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DataModule_ProvideDownloadedModelDaoFactory implements Factory<DownloadedModelDao> {
  private final Provider<LocianDatabase> dbProvider;

  public DataModule_ProvideDownloadedModelDaoFactory(Provider<LocianDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DownloadedModelDao get() {
    return provideDownloadedModelDao(dbProvider.get());
  }

  public static DataModule_ProvideDownloadedModelDaoFactory create(
      Provider<LocianDatabase> dbProvider) {
    return new DataModule_ProvideDownloadedModelDaoFactory(dbProvider);
  }

  public static DownloadedModelDao provideDownloadedModelDao(LocianDatabase db) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideDownloadedModelDao(db));
  }
}
