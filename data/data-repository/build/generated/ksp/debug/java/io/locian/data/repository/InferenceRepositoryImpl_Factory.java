package io.locian.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.inference.InferenceEngine;
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
public final class InferenceRepositoryImpl_Factory implements Factory<InferenceRepositoryImpl> {
  private final Provider<InferenceEngine> inferenceEngineProvider;

  public InferenceRepositoryImpl_Factory(Provider<InferenceEngine> inferenceEngineProvider) {
    this.inferenceEngineProvider = inferenceEngineProvider;
  }

  @Override
  public InferenceRepositoryImpl get() {
    return newInstance(inferenceEngineProvider.get());
  }

  public static InferenceRepositoryImpl_Factory create(
      Provider<InferenceEngine> inferenceEngineProvider) {
    return new InferenceRepositoryImpl_Factory(inferenceEngineProvider);
  }

  public static InferenceRepositoryImpl newInstance(InferenceEngine inferenceEngine) {
    return new InferenceRepositoryImpl(inferenceEngine);
  }
}
