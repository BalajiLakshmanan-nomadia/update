package com.synchroteam.roomDB.dao;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.synchroteam.roomDB.entity.NotificationEventModels;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public final class NotificationEventModelsDao_Impl implements NotificationEventModelsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfNotificationEventModels;

  private final SharedSQLiteStatement __preparedStmtOfDeteteAllNotificationEventModels;

  private final SharedSQLiteStatement __preparedStmtOfDeteteIDNotificationEventModels;

  public NotificationEventModelsDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNotificationEventModels = new EntityInsertionAdapter<NotificationEventModels>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `NotificationEventModelsTable`(`uid`,`id`,`url`,`value`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, NotificationEventModels value) {
        stmt.bindLong(1, value.uid);
        if (value.id == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.id);
        }
        if (value.url == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.url);
        }
        if (value.value == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.value);
        }
      }
    };
    this.__preparedStmtOfDeteteAllNotificationEventModels = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM NotificationEventModelsTable";
        return _query;
      }
    };
    this.__preparedStmtOfDeteteIDNotificationEventModels = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM NotificationEventModelsTable where id = ?";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(NotificationEventModels... notificationEventModels) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfNotificationEventModels.insert(notificationEventModels);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deteteAllNotificationEventModels() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeteteAllNotificationEventModels.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeteteAllNotificationEventModels.release(_stmt);
    }
  }

  @Override
  public void deteteIDNotificationEventModels(String userId) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeteteIDNotificationEventModels.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (userId == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, userId);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeteteIDNotificationEventModels.release(_stmt);
    }
  }

  @Override
  public List<NotificationEventModels> getAllNotificationEventModels() {
    final String _sql = "SELECT * FROM NotificationEventModelsTable";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfUid = _cursor.getColumnIndexOrThrow("uid");
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfUrl = _cursor.getColumnIndexOrThrow("url");
      final int _cursorIndexOfValue = _cursor.getColumnIndexOrThrow("value");
      final List<NotificationEventModels> _result = new ArrayList<NotificationEventModels>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final NotificationEventModels _item;
        _item = new NotificationEventModels();
        _item.uid = _cursor.getInt(_cursorIndexOfUid);
        _item.id = _cursor.getString(_cursorIndexOfId);
        _item.url = _cursor.getString(_cursorIndexOfUrl);
        _item.value = _cursor.getString(_cursorIndexOfValue);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
