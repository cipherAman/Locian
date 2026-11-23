package io.locian.feature.settings;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.data.datastore.AppPreferences;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<AppPreferences> appPreferencesProvider;

  public SettingsViewModel_Factory(Provider<Context> contextProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    this.contextProvider = contextProvider;
    this.appPreferencesProvider = appPreferencesProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(contextProvider.get(), appPreferencesProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    return new SettingsViewModel_Factory(contextProvider, appPreferencesProvider);
  }

  public static SettingsViewModel newInstance(Context context, AppPreferences appPreferences) {
    return new SettingsViewModel(context, appPreferences);
  }
}
