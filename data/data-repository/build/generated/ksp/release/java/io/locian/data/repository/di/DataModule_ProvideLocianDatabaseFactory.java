package io.locian.data.repository.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.data.database.LocianDatabase;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DataModule_ProvideLocianDatabaseFactory implements Factory<LocianDatabase> {
  private final Provider<Context> contextProvider;

  public DataModule_ProvideLocianDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LocianDatabase get() {
    return provideLocianDatabase(contextProvider.get());
  }

  public static DataModule_ProvideLocianDatabaseFactory create(Provider<Context> contextProvider) {
    return new DataModule_ProvideLocianDatabaseFactory(contextProvider);
  }

  public static LocianDatabase provideLocianDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideLocianDatabase(context));
  }
}
