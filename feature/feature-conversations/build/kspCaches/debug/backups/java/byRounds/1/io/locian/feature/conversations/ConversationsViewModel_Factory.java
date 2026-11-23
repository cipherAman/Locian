package io.locian.feature.conversations;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.domain.inference.usecases.ManageConversationUseCase;
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
public final class ConversationsViewModel_Factory implements Factory<ConversationsViewModel> {
  private final Provider<ManageConversationUseCase> manageConversationUseCaseProvider;

  public ConversationsViewModel_Factory(
      Provider<ManageConversationUseCase> manageConversationUseCaseProvider) {
    this.manageConversationUseCaseProvider = manageConversationUseCaseProvider;
  }

  @Override
  public ConversationsViewModel get() {
    return newInstance(manageConversationUseCaseProvider.get());
  }

  public static ConversationsViewModel_Factory create(
      Provider<ManageConversationUseCase> manageConversationUseCaseProvider) {
    return new ConversationsViewModel_Factory(manageConversationUseCaseProvider);
  }

  public static ConversationsViewModel newInstance(
      ManageConversationUseCase manageConversationUseCase) {
    return new ConversationsViewModel(manageConversationUseCase);
  }
}
