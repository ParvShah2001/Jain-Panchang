package com.jainpanchang.`data`.db

import android.database.Cursor
import androidx.room.CoroutinesRoom
import androidx.room.EntityDeletionOrUpdateAdapter
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class SavedCityDao_Impl(
  __db: RoomDatabase,
) : SavedCityDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfSavedCityEntity: EntityInsertionAdapter<SavedCityEntity>

  private val __deletionAdapterOfSavedCityEntity: EntityDeletionOrUpdateAdapter<SavedCityEntity>

  private val __preparedStmtOfClearSelected: SharedSQLiteStatement

  private val __preparedStmtOfSetSelected: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfSavedCityEntity = object :
        EntityInsertionAdapter<SavedCityEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `saved_cities` (`id`,`name`,`state`,`country`,`latitude`,`longitude`,`timezoneId`,`isSelected`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: SavedCityEntity) {
        statement.bindLong(1, entity.id)
        statement.bindString(2, entity.name)
        statement.bindString(3, entity.state)
        statement.bindString(4, entity.country)
        statement.bindDouble(5, entity.latitude)
        statement.bindDouble(6, entity.longitude)
        statement.bindString(7, entity.timezoneId)
        val _tmp: Int = if (entity.isSelected) 1 else 0
        statement.bindLong(8, _tmp.toLong())
      }
    }
    this.__deletionAdapterOfSavedCityEntity = object :
        EntityDeletionOrUpdateAdapter<SavedCityEntity>(__db) {
      protected override fun createQuery(): String = "DELETE FROM `saved_cities` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: SavedCityEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__preparedStmtOfClearSelected = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE saved_cities SET isSelected = 0"
        return _query
      }
    }
    this.__preparedStmtOfSetSelected = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "UPDATE saved_cities SET isSelected = 1 WHERE id = ?"
        return _query
      }
    }
  }

  public override suspend fun insertCity(city: SavedCityEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfSavedCityEntity.insertAndReturnId(city)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deleteCity(city: SavedCityEntity): Int = CoroutinesRoom.execute(__db,
      true, object : Callable<Int> {
    public override fun call(): Int {
      var _total: Int = 0
      __db.beginTransaction()
      try {
        _total += __deletionAdapterOfSavedCityEntity.handle(city)
        __db.setTransactionSuccessful()
        return _total
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun clearSelected(): Int = CoroutinesRoom.execute(__db, true, object :
      Callable<Int> {
    public override fun call(): Int {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfClearSelected.acquire()
      try {
        __db.beginTransaction()
        try {
          val _result: Int = _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
          return _result
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfClearSelected.release(_stmt)
      }
    }
  })

  public override suspend fun setSelected(cityId: Long): Int = CoroutinesRoom.execute(__db, true,
      object : Callable<Int> {
    public override fun call(): Int {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSetSelected.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, cityId)
      try {
        __db.beginTransaction()
        try {
          val _result: Int = _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
          return _result
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfSetSelected.release(_stmt)
      }
    }
  })

  public override fun getAllSavedCities(): Flow<List<SavedCityEntity>> {
    val _sql: String = "SELECT * FROM saved_cities ORDER BY name ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("saved_cities"), object :
        Callable<List<SavedCityEntity>> {
      public override fun call(): List<SavedCityEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfState: Int = getColumnIndexOrThrow(_cursor, "state")
          val _cursorIndexOfCountry: Int = getColumnIndexOrThrow(_cursor, "country")
          val _cursorIndexOfLatitude: Int = getColumnIndexOrThrow(_cursor, "latitude")
          val _cursorIndexOfLongitude: Int = getColumnIndexOrThrow(_cursor, "longitude")
          val _cursorIndexOfTimezoneId: Int = getColumnIndexOrThrow(_cursor, "timezoneId")
          val _cursorIndexOfIsSelected: Int = getColumnIndexOrThrow(_cursor, "isSelected")
          val _result: MutableList<SavedCityEntity> = ArrayList<SavedCityEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: SavedCityEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpState: String
            _tmpState = _cursor.getString(_cursorIndexOfState)
            val _tmpCountry: String
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry)
            val _tmpLatitude: Double
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude)
            val _tmpLongitude: Double
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude)
            val _tmpTimezoneId: String
            _tmpTimezoneId = _cursor.getString(_cursorIndexOfTimezoneId)
            val _tmpIsSelected: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsSelected)
            _tmpIsSelected = _tmp != 0
            _item =
                SavedCityEntity(_tmpId,_tmpName,_tmpState,_tmpCountry,_tmpLatitude,_tmpLongitude,_tmpTimezoneId,_tmpIsSelected)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun getSelectedCity(): Flow<SavedCityEntity?> {
    val _sql: String = "SELECT * FROM saved_cities WHERE isSelected = 1 LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("saved_cities"), object :
        Callable<SavedCityEntity?> {
      public override fun call(): SavedCityEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfState: Int = getColumnIndexOrThrow(_cursor, "state")
          val _cursorIndexOfCountry: Int = getColumnIndexOrThrow(_cursor, "country")
          val _cursorIndexOfLatitude: Int = getColumnIndexOrThrow(_cursor, "latitude")
          val _cursorIndexOfLongitude: Int = getColumnIndexOrThrow(_cursor, "longitude")
          val _cursorIndexOfTimezoneId: Int = getColumnIndexOrThrow(_cursor, "timezoneId")
          val _cursorIndexOfIsSelected: Int = getColumnIndexOrThrow(_cursor, "isSelected")
          val _result: SavedCityEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpState: String
            _tmpState = _cursor.getString(_cursorIndexOfState)
            val _tmpCountry: String
            _tmpCountry = _cursor.getString(_cursorIndexOfCountry)
            val _tmpLatitude: Double
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude)
            val _tmpLongitude: Double
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude)
            val _tmpTimezoneId: String
            _tmpTimezoneId = _cursor.getString(_cursorIndexOfTimezoneId)
            val _tmpIsSelected: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsSelected)
            _tmpIsSelected = _tmp != 0
            _result =
                SavedCityEntity(_tmpId,_tmpName,_tmpState,_tmpCountry,_tmpLatitude,_tmpLongitude,_tmpTimezoneId,_tmpIsSelected)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
