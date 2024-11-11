package com.synchroteam.roomDB.dao;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.synchroteam.roomDB.entity.LocationCoordinates;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public final class LocationCoordinatesDao_Impl implements LocationCoordinatesDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfLocationCoordinates;

  private final SharedSQLiteStatement __preparedStmtOfDeteteAllLocationCoordinatesModels;

  public LocationCoordinatesDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLocationCoordinates = new EntityInsertionAdapter<LocationCoordinates>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `LocationCoordinatesTable`(`uid`,`latitude`,`longitude`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, LocationCoordinates value) {
        stmt.bindLong(1, value.uid);
        stmt.bindDouble(2, value.latitude);
        stmt.bindDouble(3, value.getLongitude());
      }
    };
    this.__preparedStmtOfDeteteAllLocationCoordinatesModels = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM LocationCoordinatesTable";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(LocationCoordinates... locationCoordinates) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfLocationCoordinates.insert(locationCoordinates);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deteteAllLocationCoordinatesModels() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeteteAllLocationCoordinatesModels.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeteteAllLocationCoordinatesModels.release(_stmt);
    }
  }

  @Override
  public List<LocationCoordinates> getAllLocationCoordinatesModels() {
    final String _sql = "SELECT * FROM LocationCoordinatesTable";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfUid = _cursor.getColumnIndexOrThrow("uid");
      final int _cursorIndexOfLatitude = _cursor.getColumnIndexOrThrow("latitude");
      final int _cursorIndexOfLongitude = _cursor.getColumnIndexOrThrow("longitude");
      final List<LocationCoordinates> _result = new ArrayList<LocationCoordinates>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final LocationCoordinates _item;
        _item = new LocationCoordinates();
        _item.uid = _cursor.getInt(_cursorIndexOfUid);
        _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
        final double _tmpLongitude;
        _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
        _item.setLongitude(_tmpLongitude);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
