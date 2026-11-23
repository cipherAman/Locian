package io.locian.data.repository.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.inference.InferenceEngine;
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
public final class DataModule_ProvideInferenceEngineFactory implements Factory<InferenceEngine> {
  @Override
  public InferenceEngine get() {
    return provideInferenceEngine();
  }

  public static DataModule_ProvideInferenceEngineFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static InferenceEngine provideInferenceEngine() {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideInferenceEngine());
  }

  private static final class InstanceHolder {
    private static final DataModule_ProvideInferenceEngineFactory INSTANCE = new DataModule_ProvideInferenceEngineFactory();
  }
}
