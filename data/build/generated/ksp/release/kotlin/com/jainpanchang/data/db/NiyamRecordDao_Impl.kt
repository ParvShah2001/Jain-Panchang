package com.jainpanchang.`data`.db

import android.database.Cursor
import androidx.room.CoroutinesRoom
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class NiyamRecordDao_Impl(
  __db: RoomDatabase,
) : NiyamRecordDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfNiyamRecordEntity: EntityInsertionAdapter<NiyamRecordEntity>
  init {
    this.__db = __db
    this.__insertionAdapterOfNiyamRecordEntity = object :
        EntityInsertionAdapter<NiyamRecordEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `niyam_records` (`dateIso`,`niyamId`,`completed`,`customMaryada`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: NiyamRecordEntity) {
        statement.bindString(1, entity.dateIso)
        statement.bindLong(2, entity.niyamId.toLong())
        val _tmp: Int = if (entity.completed) 1 else 0
        statement.bindLong(3, _tmp.toLong())
        statement.bindString(4, entity.customMaryada)
      }
    }
  }

  public override suspend fun insertOrUpdate(record: NiyamRecordEntity): Long =
      CoroutinesRoom.execute(__db, true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfNiyamRecordEntity.insertAndReturnId(record)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override fun getRecordsForDate(dateIso: String): Flow<List<NiyamRecordEntity>> {
    val _sql: String = "SELECT * FROM niyam_records WHERE dateIso = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, dateIso)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("niyam_records"), object :
        Callable<List<NiyamRecordEntity>> {
      public override fun call(): List<NiyamRecordEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfDateIso: Int = getColumnIndexOrThrow(_cursor, "dateIso")
          val _cursorIndexOfNiyamId: Int = getColumnIndexOrThrow(_cursor, "niyamId")
          val _cursorIndexOfCompleted: Int = getColumnIndexOrThrow(_cursor, "completed")
          val _cursorIndexOfCustomMaryada: Int = getColumnIndexOrThrow(_cursor, "customMaryada")
          val _result: MutableList<NiyamRecordEntity> =
              ArrayList<NiyamRecordEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: NiyamRecordEntity
            val _tmpDateIso: String
            _tmpDateIso = _cursor.getString(_cursorIndexOfDateIso)
            val _tmpNiyamId: Int
            _tmpNiyamId = _cursor.getInt(_cursorIndexOfNiyamId)
            val _tmpCompleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfCompleted)
            _tmpCompleted = _tmp != 0
            val _tmpCustomMaryada: String
            _tmpCustomMaryada = _cursor.getString(_cursorIndexOfCustomMaryada)
            _item = NiyamRecordEntity(_tmpDateIso,_tmpNiyamId,_tmpCompleted,_tmpCustomMaryada)
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

  public override fun getRecentRecords(startDateIso: String): Flow<List<NiyamRecordEntity>> {
    val _sql: String = "SELECT * FROM niyam_records WHERE dateIso >= ? ORDER BY dateIso ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, startDateIso)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("niyam_records"), object :
        Callable<List<NiyamRecordEntity>> {
      public override fun call(): List<NiyamRecordEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfDateIso: Int = getColumnIndexOrThrow(_cursor, "dateIso")
          val _cursorIndexOfNiyamId: Int = getColumnIndexOrThrow(_cursor, "niyamId")
          val _cursorIndexOfCompleted: Int = getColumnIndexOrThrow(_cursor, "completed")
          val _cursorIndexOfCustomMaryada: Int = getColumnIndexOrThrow(_cursor, "customMaryada")
          val _result: MutableList<NiyamRecordEntity> =
              ArrayList<NiyamRecordEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: NiyamRecordEntity
            val _tmpDateIso: String
            _tmpDateIso = _cursor.getString(_cursorIndexOfDateIso)
            val _tmpNiyamId: Int
            _tmpNiyamId = _cursor.getInt(_cursorIndexOfNiyamId)
            val _tmpCompleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfCompleted)
            _tmpCompleted = _tmp != 0
            val _tmpCustomMaryada: String
            _tmpCustomMaryada = _cursor.getString(_cursorIndexOfCustomMaryada)
            _item = NiyamRecordEntity(_tmpDateIso,_tmpNiyamId,_tmpCompleted,_tmpCustomMaryada)
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

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
