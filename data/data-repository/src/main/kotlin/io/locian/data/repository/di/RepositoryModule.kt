package io.locian.data.repository.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.locian.data.database.LocianDatabase
import io.locian.data.database.dao.ConversationDao
import io.locian.data.database.dao.DownloadedModelDao
import io.locian.data.database.dao.MessageDao
import io.locian.data.datastore.AppPreferences
import io.locian.data.network.ModelDownloader
import io.locian.data.repository.ChatRepositoryImpl
import io.locian.data.repository.InferenceRepositoryImpl
import io.locian.data.repository.ModelRepositoryImpl
import io.locian.domain.inference.repository.ChatRepository
import io.locian.domain.inference.repository.InferenceRepository
import io.locian.domain.inference.repository.ModelRepository
import io.locian.inference.InferenceEngine
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindsModule {

    @Binds
    @Singleton
    abstract fun bindInferenceRepository(impl: InferenceRepositoryImpl): InferenceRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindModelRepository(impl: ModelRepositoryImpl): ModelRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideInferenceEngine(): InferenceEngine = InferenceEngine()

    @Provides
    @Singleton
    fun provideLocianDatabase(@ApplicationContext context: Context): LocianDatabase {
        return Room.databaseBuilder(
            context,
            LocianDatabase::class.java,
            "locian.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideConversationDao(db: LocianDatabase): ConversationDao = db.conversationDao()

    @Provides
    fun provideMessageDao(db: LocianDatabase): MessageDao = db.messageDao()

    @Provides
    fun provideDownloadedModelDao(db: LocianDatabase): DownloadedModelDao = db.downloadedModelDao()

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }

    @Provides
    @Singleton
    fun provideModelDownloader(): ModelDownloader = ModelDownloader()
}
