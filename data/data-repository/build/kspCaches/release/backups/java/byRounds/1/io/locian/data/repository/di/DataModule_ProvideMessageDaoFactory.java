package io.locian.data.repository.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.data.database.LocianDatabase;
import io.locian.data.database.dao.MessageDao;
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
public final class DataModule_ProvideMessageDaoFactory implements Factory<MessageDao> {
  private final Provider<LocianDatabase> dbProvider;

  public DataModule_ProvideMessageDaoFactory(Provider<LocianDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public MessageDao get() {
    return provideMessageDao(dbProvider.get());
  }

  public static DataModule_ProvideMessageDaoFactory create(Provider<LocianDatabase> dbProvider) {
    return new DataModule_ProvideMessageDaoFactory(dbProvider);
  }

  public static MessageDao provideMessageDao(LocianDatabase db) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideMessageDao(db));
  }
}
