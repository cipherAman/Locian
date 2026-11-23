package io.locian.data.database.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import io.locian.data.database.entities.MessageEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MessageDao_Impl implements MessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MessageEntity> __insertionAdapterOfMessageEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMessagesForConversation;

  public MessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMessageEntity = new EntityInsertionAdapter<MessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `messages` (`id`,`conversationId`,`role`,`content`,`timestamp`,`tokensPerSecond`,`timeToFirstTokenMs`,`totalTokens`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MessageEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindLong(2, entity.getConversationId());
        statement.bindString(3, entity.getRole());
        statement.bindString(4, entity.getContent());
        statement.bindLong(5, entity.getTimestamp());
        if (entity.getTokensPerSecond() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getTokensPerSecond());
        }
        if (entity.getTimeToFirstTokenMs() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getTimeToFirstTokenMs());
        }
        if (entity.getTotalTokens() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getTotalTokens());
        }
      }
    };
    this.__preparedStmtOfDeleteMessagesForConversation = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM messages WHERE conversationId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMessage(final MessageEntity message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMessageEntity.insert(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMessages(final List<MessageEntity> messages,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMessageEntity.insert(messages);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMessagesForConversation(final long conversationId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMessagesForConversation.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, conversationId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteMessagesForConversation.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MessageEntity>> getMessagesForConversation(final long conversationId) {
    final String _sql = "SELECT * FROM messages WHERE conversationId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, conversationId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"messages"}, new Callable<List<MessageEntity>>() {
      @Override
      @NonNull
      public List<MessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfTokensPerSecond = CursorUtil.getColumnIndexOrThrow(_cursor, "tokensPerSecond");
          final int _cursorIndexOfTimeToFirstTokenMs = CursorUtil.getColumnIndexOrThrow(_cursor, "timeToFirstTokenMs");
          final int _cursorIndexOfTotalTokens = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTokens");
          final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MessageEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final long _tmpConversationId;
            _tmpConversationId = _cursor.getLong(_cursorIndexOfConversationId);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final Float _tmpTokensPerSecond;
            if (_cursor.isNull(_cursorIndexOfTokensPerSecond)) {
              _tmpTokensPerSecond = null;
            } else {
              _tmpTokensPerSecond = _cursor.getFloat(_cursorIndexOfTokensPerSecond);
            }
            final Long _tmpTimeToFirstTokenMs;
            if (_cursor.isNull(_cursorIndexOfTimeToFirstTokenMs)) {
              _tmpTimeToFirstTokenMs = null;
            } else {
              _tmpTimeToFirstTokenMs = _cursor.getLong(_cursorIndexOfTimeToFirstTokenMs);
            }
            final Integer _tmpTotalTokens;
            if (_cursor.isNull(_cursorIndexOfTotalTokens)) {
              _tmpTotalTokens = null;
            } else {
              _tmpTotalTokens = _cursor.getInt(_cursorIndexOfTotalTokens);
            }
            _item = new MessageEntity(_tmpId,_tmpConversationId,_tmpRole,_tmpContent,_tmpTimestamp,_tmpTokensPerSecond,_tmpTimeToFirstTokenMs,_tmpTotalTokens);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
