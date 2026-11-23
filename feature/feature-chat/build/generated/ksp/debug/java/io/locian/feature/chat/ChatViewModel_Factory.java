package io.locian.feature.chat;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.data.datastore.AppPreferences;
import io.locian.domain.inference.usecases.ManageConversationUseCase;
import io.locian.domain.inference.usecases.StreamChatResponseUseCase;
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
public final class ChatViewModel_Factory implements Factory<ChatViewModel> {
  private final Provider<StreamChatResponseUseCase> streamChatResponseUseCaseProvider;

  private final Provider<ManageConversationUseCase> manageConversationUseCaseProvider;

  private final Provider<AppPreferences> appPreferencesProvider;

  public ChatViewModel_Factory(
      Provider<StreamChatResponseUseCase> streamChatResponseUseCaseProvider,
      Provider<ManageConversationUseCase> manageConversationUseCaseProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    this.streamChatResponseUseCaseProvider = streamChatResponseUseCaseProvider;
    this.manageConversationUseCaseProvider = manageConversationUseCaseProvider;
    this.appPreferencesProvider = appPreferencesProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(streamChatResponseUseCaseProvider.get(), manageConversationUseCaseProvider.get(), appPreferencesProvider.get());
  }

  public static ChatViewModel_Factory create(
      Provider<StreamChatResponseUseCase> streamChatResponseUseCaseProvider,
      Provider<ManageConversationUseCase> manageConversationUseCaseProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    return new ChatViewModel_Factory(streamChatResponseUseCaseProvider, manageConversationUseCaseProvider, appPreferencesProvider);
  }

  public static ChatViewModel newInstance(StreamChatResponseUseCase streamChatResponseUseCase,
      ManageConversationUseCase manageConversationUseCase, AppPreferences appPreferences) {
    return new ChatViewModel(streamChatResponseUseCase, manageConversationUseCase, appPreferences);
  }
}
