package com.jainpanchang.`data`.db

import android.database.Cursor
import androidx.room.CoroutinesRoom
import androidx.room.EntityDeletionOrUpdateAdapter
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
public class MyEventDao_Impl(
  __db: RoomDatabase,
) : MyEventDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfMyEventEntity: EntityInsertionAdapter<MyEventEntity>

  private val __deletionAdapterOfMyEventEntity: EntityDeletionOrUpdateAdapter<MyEventEntity>

  private val __updateAdapterOfMyEventEntity: EntityDeletionOrUpdateAdapter<MyEventEntity>
  init {
    this.__db = __db
    this.__insertionAdapterOfMyEventEntity = object : EntityInsertionAdapter<MyEventEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `my_events` (`id`,`title`,`notes`,`isTithiBased`,`jainMonth`,`paksha`,`tithi`,`gregorianMonth`,`gregorianDay`,`reminderHour`,`reminderMinute`,`isReminderEnabled`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: MyEventEntity) {
        statement.bindLong(1, entity.id)
        statement.bindString(2, entity.title)
        statement.bindString(3, entity.notes)
        val _tmp: Int = if (entity.isTithiBased) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindString(5, entity.jainMonth)
        statement.bindString(6, entity.paksha)
        statement.bindLong(7, entity.tithi.toLong())
        statement.bindLong(8, entity.gregorianMonth.toLong())
        statement.bindLong(9, entity.gregorianDay.toLong())
        statement.bindLong(10, entity.reminderHour.toLong())
        statement.bindLong(11, entity.reminderMinute.toLong())
        val _tmp_1: Int = if (entity.isReminderEnabled) 1 else 0
        statement.bindLong(12, _tmp_1.toLong())
      }
    }
    this.__deletionAdapterOfMyEventEntity = object :
        EntityDeletionOrUpdateAdapter<MyEventEntity>(__db) {
      protected override fun createQuery(): String = "DELETE FROM `my_events` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: MyEventEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfMyEventEntity = object :
        EntityDeletionOrUpdateAdapter<MyEventEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `my_events` SET `id` = ?,`title` = ?,`notes` = ?,`isTithiBased` = ?,`jainMonth` = ?,`paksha` = ?,`tithi` = ?,`gregorianMonth` = ?,`gregorianDay` = ?,`reminderHour` = ?,`reminderMinute` = ?,`isReminderEnabled` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: MyEventEntity) {
        statement.bindLong(1, entity.id)
        statement.bindString(2, entity.title)
        statement.bindString(3, entity.notes)
        val _tmp: Int = if (entity.isTithiBased) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindString(5, entity.jainMonth)
        statement.bindString(6, entity.paksha)
        statement.bindLong(7, entity.tithi.toLong())
        statement.bindLong(8, entity.gregorianMonth.toLong())
        statement.bindLong(9, entity.gregorianDay.toLong())
        statement.bindLong(10, entity.reminderHour.toLong())
        statement.bindLong(11, entity.reminderMinute.toLong())
        val _tmp_1: Int = if (entity.isReminderEnabled) 1 else 0
        statement.bindLong(12, _tmp_1.toLong())
        statement.bindLong(13, entity.id)
      }
    }
  }

  public override suspend fun insertEvent(event: MyEventEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfMyEventEntity.insertAndReturnId(event)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deleteEvent(event: MyEventEntity): Int = CoroutinesRoom.execute(__db,
      true, object : Callable<Int> {
    public override fun call(): Int {
      var _total: Int = 0
      __db.beginTransaction()
      try {
        _total += __deletionAdapterOfMyEventEntity.handle(event)
        __db.setTransactionSuccessful()
        return _total
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateEvent(event: MyEventEntity): Int = CoroutinesRoom.execute(__db,
      true, object : Callable<Int> {
    public override fun call(): Int {
      var _total: Int = 0
      __db.beginTransaction()
      try {
        _total += __updateAdapterOfMyEventEntity.handle(event)
        __db.setTransactionSuccessful()
        return _total
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override fun getAllEvents(): Flow<List<MyEventEntity>> {
    val _sql: String = "SELECT * FROM my_events ORDER BY id DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("my_events"), object :
        Callable<List<MyEventEntity>> {
      public override fun call(): List<MyEventEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfNotes: Int = getColumnIndexOrThrow(_cursor, "notes")
          val _cursorIndexOfIsTithiBased: Int = getColumnIndexOrThrow(_cursor, "isTithiBased")
          val _cursorIndexOfJainMonth: Int = getColumnIndexOrThrow(_cursor, "jainMonth")
          val _cursorIndexOfPaksha: Int = getColumnIndexOrThrow(_cursor, "paksha")
          val _cursorIndexOfTithi: Int = getColumnIndexOrThrow(_cursor, "tithi")
          val _cursorIndexOfGregorianMonth: Int = getColumnIndexOrThrow(_cursor, "gregorianMonth")
          val _cursorIndexOfGregorianDay: Int = getColumnIndexOrThrow(_cursor, "gregorianDay")
          val _cursorIndexOfReminderHour: Int = getColumnIndexOrThrow(_cursor, "reminderHour")
          val _cursorIndexOfReminderMinute: Int = getColumnIndexOrThrow(_cursor, "reminderMinute")
          val _cursorIndexOfIsReminderEnabled: Int = getColumnIndexOrThrow(_cursor,
              "isReminderEnabled")
          val _result: MutableList<MyEventEntity> = ArrayList<MyEventEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: MyEventEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpNotes: String
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes)
            val _tmpIsTithiBased: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsTithiBased)
            _tmpIsTithiBased = _tmp != 0
            val _tmpJainMonth: String
            _tmpJainMonth = _cursor.getString(_cursorIndexOfJainMonth)
            val _tmpPaksha: String
            _tmpPaksha = _cursor.getString(_cursorIndexOfPaksha)
            val _tmpTithi: Int
            _tmpTithi = _cursor.getInt(_cursorIndexOfTithi)
            val _tmpGregorianMonth: Int
            _tmpGregorianMonth = _cursor.getInt(_cursorIndexOfGregorianMonth)
            val _tmpGregorianDay: Int
            _tmpGregorianDay = _cursor.getInt(_cursorIndexOfGregorianDay)
            val _tmpReminderHour: Int
            _tmpReminderHour = _cursor.getInt(_cursorIndexOfReminderHour)
            val _tmpReminderMinute: Int
            _tmpReminderMinute = _cursor.getInt(_cursorIndexOfReminderMinute)
            val _tmpIsReminderEnabled: Boolean
            val _tmp_1: Int
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsReminderEnabled)
            _tmpIsReminderEnabled = _tmp_1 != 0
            _item =
                MyEventEntity(_tmpId,_tmpTitle,_tmpNotes,_tmpIsTithiBased,_tmpJainMonth,_tmpPaksha,_tmpTithi,_tmpGregorianMonth,_tmpGregorianDay,_tmpReminderHour,_tmpReminderMinute,_tmpIsReminderEnabled)
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
