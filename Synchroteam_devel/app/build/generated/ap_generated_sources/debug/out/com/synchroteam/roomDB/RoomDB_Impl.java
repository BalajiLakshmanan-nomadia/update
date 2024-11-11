package com.synchroteam.roomDB;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import com.synchroteam.roomDB.dao.LocationCoordinatesDao;
import com.synchroteam.roomDB.dao.LocationCoordinatesDao_Impl;
import com.synchroteam.roomDB.dao.NotificationEventModelsDao;
import com.synchroteam.roomDB.dao.NotificationEventModelsDao_Impl;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unchecked")
public final class RoomDB_Impl extends RoomDB {
  private volatile NotificationEventModelsDao _notificationEventModelsDao;

  private volatile LocationCoordinatesDao _locationCoordinatesDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `LocationCoordinatesTable` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `NotifEventModelsTable` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `id` TEXT, `url` TEXT, `value` TEXT, `isDelete` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `NotificationEventModelsTable` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `id` TEXT, `url` TEXT, `value` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"6e76e1847aea1b5c4e96ec20c79ca04e\")");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `LocationCoordinatesTable`");
        _db.execSQL("DROP TABLE IF EXISTS `NotifEventModelsTable`");
        _db.execSQL("DROP TABLE IF EXISTS `NotificationEventModelsTable`");
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsLocationCoordinatesTable = new HashMap<String, TableInfo.Column>(3);
        _columnsLocationCoordinatesTable.put("uid", new TableInfo.Column("uid", "INTEGER", true, 1));
        _columnsLocationCoordinatesTable.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0));
        _columnsLocationCoordinatesTable.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLocationCoordinatesTable = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLocationCoordinatesTable = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLocationCoordinatesTable = new TableInfo("LocationCoordinatesTable", _columnsLocationCoordinatesTable, _foreignKeysLocationCoordinatesTable, _indicesLocationCoordinatesTable);
        final TableInfo _existingLocationCoordinatesTable = TableInfo.read(_db, "LocationCoordinatesTable");
        if (! _infoLocationCoordinatesTable.equals(_existingLocationCoordinatesTable)) {
          throw new IllegalStateException("Migration didn't properly handle LocationCoordinatesTable(com.synchroteam.roomDB.entity.LocationCoordinates).\n"
                  + " Expected:\n" + _infoLocationCoordinatesTable + "\n"
                  + " Found:\n" + _existingLocationCoordinatesTable);
        }
        final HashMap<String, TableInfo.Column> _columnsNotifEventModelsTable = new HashMap<String, TableInfo.Column>(5);
        _columnsNotifEventModelsTable.put("uid", new TableInfo.Column("uid", "INTEGER", true, 1));
        _columnsNotifEventModelsTable.put("id", new TableInfo.Column("id", "TEXT", false, 0));
        _columnsNotifEventModelsTable.put("url", new TableInfo.Column("url", "TEXT", false, 0));
        _columnsNotifEventModelsTable.put("value", new TableInfo.Column("value", "TEXT", false, 0));
        _columnsNotifEventModelsTable.put("isDelete", new TableInfo.Column("isDelete", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotifEventModelsTable = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotifEventModelsTable = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotifEventModelsTable = new TableInfo("NotifEventModelsTable", _columnsNotifEventModelsTable, _foreignKeysNotifEventModelsTable, _indicesNotifEventModelsTable);
        final TableInfo _existingNotifEventModelsTable = TableInfo.read(_db, "NotifEventModelsTable");
        if (! _infoNotifEventModelsTable.equals(_existingNotifEventModelsTable)) {
          throw new IllegalStateException("Migration didn't properly handle NotifEventModelsTable(com.synchroteam.roomDB.entity.NotifEventModels).\n"
                  + " Expected:\n" + _infoNotifEventModelsTable + "\n"
                  + " Found:\n" + _existingNotifEventModelsTable);
        }
        final HashMap<String, TableInfo.Column> _columnsNotificationEventModelsTable = new HashMap<String, TableInfo.Column>(4);
        _columnsNotificationEventModelsTable.put("uid", new TableInfo.Column("uid", "INTEGER", true, 1));
        _columnsNotificationEventModelsTable.put("id", new TableInfo.Column("id", "TEXT", false, 0));
        _columnsNotificationEventModelsTable.put("url", new TableInfo.Column("url", "TEXT", false, 0));
        _columnsNotificationEventModelsTable.put("value", new TableInfo.Column("value", "TEXT", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotificationEventModelsTable = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotificationEventModelsTable = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotificationEventModelsTable = new TableInfo("NotificationEventModelsTable", _columnsNotificationEventModelsTable, _foreignKeysNotificationEventModelsTable, _indicesNotificationEventModelsTable);
        final TableInfo _existingNotificationEventModelsTable = TableInfo.read(_db, "NotificationEventModelsTable");
        if (! _infoNotificationEventModelsTable.equals(_existingNotificationEventModelsTable)) {
          throw new IllegalStateException("Migration didn't properly handle NotificationEventModelsTable(com.synchroteam.roomDB.entity.NotificationEventModels).\n"
                  + " Expected:\n" + _infoNotificationEventModelsTable + "\n"
                  + " Found:\n" + _existingNotificationEventModelsTable);
        }
      }
    }, "6e76e1847aea1b5c4e96ec20c79ca04e", "03aa2530f614b337bd685c050b7e1fe2");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "LocationCoordinatesTable","NotifEventModelsTable","NotificationEventModelsTable");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `LocationCoordinatesTable`");
      _db.execSQL("DELETE FROM `NotifEventModelsTable`");
      _db.execSQL("DELETE FROM `NotificationEventModelsTable`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public NotificationEventModelsDao roomDao() {
    if (_notificationEventModelsDao != null) {
      return _notificationEventModelsDao;
    } else {
      synchronized(this) {
        if(_notificationEventModelsDao == null) {
          _notificationEventModelsDao = new NotificationEventModelsDao_Impl(this);
        }
        return _notificationEventModelsDao;
      }
    }
  }

  @Override
  public LocationCoordinatesDao LocationCoordinatesDao() {
    if (_locationCoordinatesDao != null) {
      return _locationCoordinatesDao;
    } else {
      synchronized(this) {
        if(_locationCoordinatesDao == null) {
          _locationCoordinatesDao = new LocationCoordinatesDao_Impl(this);
        }
        return _locationCoordinatesDao;
      }
    }
  }
}
