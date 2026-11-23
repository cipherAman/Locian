package io.locian.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.locian.data.database.dao.ConversationDao;
import io.locian.data.database.dao.MessageDao;
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
public final class ChatRepositoryImpl_Factory implements Factory<ChatRepositoryImpl> {
  private final Provider<ConversationDao> conversationDaoProvider;

  private final Provider<MessageDao> messageDaoProvider;

  public ChatRepositoryImpl_Factory(Provider<ConversationDao> conversationDaoProvider,
      Provider<MessageDao> messageDaoProvider) {
    this.conversationDaoProvider = conversationDaoProvider;
    this.messageDaoProvider = messageDaoProvider;
  }

  @Override
  public ChatRepositoryImpl get() {
    return newInstance(conversationDaoProvider.get(), messageDaoProvider.get());
  }

  public static ChatRepositoryImpl_Factory create(Provider<ConversationDao> conversationDaoProvider,
      Provider<MessageDao> messageDaoProvider) {
    return new ChatRepositoryImpl_Factory(conversationDaoProvider, messageDaoProvider);
  }

  public static ChatRepositoryImpl newInstance(ConversationDao conversationDao,
      MessageDao messageDao) {
    return new ChatRepositoryImpl(conversationDao, messageDao);
  }
}
