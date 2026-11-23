package io.locian.data.repository.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.data.database.LocianDatabase;
import io.locian.data.database.dao.ConversationDao;
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
public final class DataModule_ProvideConversationDaoFactory implements Factory<ConversationDao> {
  private final Provider<LocianDatabase> dbProvider;

  public DataModule_ProvideConversationDaoFactory(Provider<LocianDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ConversationDao get() {
    return provideConversationDao(dbProvider.get());
  }

  public static DataModule_ProvideConversationDaoFactory create(
      Provider<LocianDatabase> dbProvider) {
    return new DataModule_ProvideConversationDaoFactory(dbProvider);
  }

  public static ConversationDao provideConversationDao(LocianDatabase db) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideConversationDao(db));
  }
}
