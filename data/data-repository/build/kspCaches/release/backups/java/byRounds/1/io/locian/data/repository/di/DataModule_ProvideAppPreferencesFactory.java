package io.locian.data.repository.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.data.datastore.AppPreferences;
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
public final class DataModule_ProvideAppPreferencesFactory implements Factory<AppPreferences> {
  private final Provider<Context> contextProvider;

  public DataModule_ProvideAppPreferencesFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AppPreferences get() {
    return provideAppPreferences(contextProvider.get());
  }

  public static DataModule_ProvideAppPreferencesFactory create(Provider<Context> contextProvider) {
    return new DataModule_ProvideAppPreferencesFactory(contextProvider);
  }

  public static AppPreferences provideAppPreferences(Context context) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideAppPreferences(context));
  }
}
