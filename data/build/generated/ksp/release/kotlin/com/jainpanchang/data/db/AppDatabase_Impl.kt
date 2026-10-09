package com.jainpanchang.`data`.db

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.room.RoomOpenHelper
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import java.lang.Class
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import javax.`annotation`.processing.Generated
import kotlin.Any
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.Set

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class AppDatabase_Impl : AppDatabase() {
  private val _savedCityDao: Lazy<SavedCityDao> = lazy {
    SavedCityDao_Impl(this)
  }


  private val _myEventDao: Lazy<MyEventDao> = lazy {
    MyEventDao_Impl(this)
  }


  private val _niyamRecordDao: Lazy<NiyamRecordDao> = lazy {
    NiyamRecordDao_Impl(this)
  }


  protected override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
    val _openCallback: SupportSQLiteOpenHelper.Callback = RoomOpenHelper(config, object :
        RoomOpenHelper.Delegate(1) {
      public override fun createAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `saved_cities` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `state` TEXT NOT NULL, `country` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `timezoneId` TEXT NOT NULL, `isSelected` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `my_events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `notes` TEXT NOT NULL, `isTithiBased` INTEGER NOT NULL, `jainMonth` TEXT NOT NULL, `paksha` TEXT NOT NULL, `tithi` INTEGER NOT NULL, `gregorianMonth` INTEGER NOT NULL, `gregorianDay` INTEGER NOT NULL, `reminderHour` INTEGER NOT NULL, `reminderMinute` INTEGER NOT NULL, `isReminderEnabled` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `niyam_records` (`dateIso` TEXT NOT NULL, `niyamId` INTEGER NOT NULL, `completed` INTEGER NOT NULL, `customMaryada` TEXT NOT NULL, PRIMARY KEY(`dateIso`, `niyamId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c7fb2386e3bbd4253c5c8eb3082bacfa')")
      }

      public override fun dropAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `saved_cities`")
        db.execSQL("DROP TABLE IF EXISTS `my_events`")
        db.execSQL("DROP TABLE IF EXISTS `niyam_records`")
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onDestructiveMigration(db)
          }
        }
      }

      public override fun onCreate(db: SupportSQLiteDatabase) {
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onCreate(db)
          }
        }
      }

      public override fun onOpen(db: SupportSQLiteDatabase) {
        mDatabase = db
        internalInitInvalidationTracker(db)
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onOpen(db)
          }
        }
      }

      public override fun onPreMigrate(db: SupportSQLiteDatabase) {
        dropFtsSyncTriggers(db)
      }

      public override fun onPostMigrate(db: SupportSQLiteDatabase) {
      }

      public override fun onValidateSchema(db: SupportSQLiteDatabase):
          RoomOpenHelper.ValidationResult {
        val _columnsSavedCities: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(8)
        _columnsSavedCities.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedCities.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedCities.put("state", TableInfo.Column("state", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedCities.put("country", TableInfo.Column("country", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedCities.put("latitude", TableInfo.Column("latitude", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedCities.put("longitude", TableInfo.Column("longitude", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedCities.put("timezoneId", TableInfo.Column("timezoneId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedCities.put("isSelected", TableInfo.Column("isSelected", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSavedCities: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesSavedCities: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoSavedCities: TableInfo = TableInfo("saved_cities", _columnsSavedCities,
            _foreignKeysSavedCities, _indicesSavedCities)
        val _existingSavedCities: TableInfo = read(db, "saved_cities")
        if (!_infoSavedCities.equals(_existingSavedCities)) {
          return RoomOpenHelper.ValidationResult(false, """
              |saved_cities(com.jainpanchang.data.db.SavedCityEntity).
              | Expected:
              |""".trimMargin() + _infoSavedCities + """
              |
              | Found:
              |""".trimMargin() + _existingSavedCities)
        }
        val _columnsMyEvents: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(12)
        _columnsMyEvents.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMyEvents.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMyEvents.put("notes", TableInfo.Column("notes", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMyEvents.put("isTithiBased", TableInfo.Column("isTithiBased", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMyEvents.put("jainMonth", TableInfo.Column("jainMonth", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMyEvents.put("paksha", TableInfo.Column("paksha", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMyEvents.put("tithi", TableInfo.Column("tithi", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMyEvents.put("gregorianMonth", TableInfo.Column("gregorianMonth", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMyEvents.put("gregorianDay", TableInfo.Column("gregorianDay", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMyEvents.put("reminderHour", TableInfo.Column("reminderHour", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMyEvents.put("reminderMinute", TableInfo.Column("reminderMinute", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMyEvents.put("isReminderEnabled", TableInfo.Column("isReminderEnabled", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMyEvents: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesMyEvents: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoMyEvents: TableInfo = TableInfo("my_events", _columnsMyEvents,
            _foreignKeysMyEvents, _indicesMyEvents)
        val _existingMyEvents: TableInfo = read(db, "my_events")
        if (!_infoMyEvents.equals(_existingMyEvents)) {
          return RoomOpenHelper.ValidationResult(false, """
              |my_events(com.jainpanchang.data.db.MyEventEntity).
              | Expected:
              |""".trimMargin() + _infoMyEvents + """
              |
              | Found:
              |""".trimMargin() + _existingMyEvents)
        }
        val _columnsNiyamRecords: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(4)
        _columnsNiyamRecords.put("dateIso", TableInfo.Column("dateIso", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNiyamRecords.put("niyamId", TableInfo.Column("niyamId", "INTEGER", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNiyamRecords.put("completed", TableInfo.Column("completed", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNiyamRecords.put("customMaryada", TableInfo.Column("customMaryada", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysNiyamRecords: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesNiyamRecords: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoNiyamRecords: TableInfo = TableInfo("niyam_records", _columnsNiyamRecords,
            _foreignKeysNiyamRecords, _indicesNiyamRecords)
        val _existingNiyamRecords: TableInfo = read(db, "niyam_records")
        if (!_infoNiyamRecords.equals(_existingNiyamRecords)) {
          return RoomOpenHelper.ValidationResult(false, """
              |niyam_records(com.jainpanchang.data.db.NiyamRecordEntity).
              | Expected:
              |""".trimMargin() + _infoNiyamRecords + """
              |
              | Found:
              |""".trimMargin() + _existingNiyamRecords)
        }
        return RoomOpenHelper.ValidationResult(true, null)
      }
    }, "c7fb2386e3bbd4253c5c8eb3082bacfa", "91c6aa3893c4b3361c6b03c7abb58fd9")
    val _sqliteConfig: SupportSQLiteOpenHelper.Configuration =
        SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build()
    val _helper: SupportSQLiteOpenHelper = config.sqliteOpenHelperFactory.create(_sqliteConfig)
    return _helper
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: HashMap<String, String> = HashMap<String, String>(0)
    val _viewTables: HashMap<String, Set<String>> = HashMap<String, Set<String>>(0)
    return InvalidationTracker(this, _shadowTablesMap, _viewTables,
        "saved_cities","my_events","niyam_records")
  }

  public override fun clearAllTables() {
    super.assertNotMainThread()
    val _db: SupportSQLiteDatabase = super.openHelper.writableDatabase
    try {
      super.beginTransaction()
      _db.execSQL("DELETE FROM `saved_cities`")
      _db.execSQL("DELETE FROM `my_events`")
      _db.execSQL("DELETE FROM `niyam_records`")
      super.setTransactionSuccessful()
    } finally {
      super.endTransaction()
      _db.query("PRAGMA wal_checkpoint(FULL)").close()
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM")
      }
    }
  }

  protected override fun getRequiredTypeConverters(): Map<Class<out Any>, List<Class<out Any>>> {
    val _typeConvertersMap: HashMap<Class<out Any>, List<Class<out Any>>> =
        HashMap<Class<out Any>, List<Class<out Any>>>()
    _typeConvertersMap.put(SavedCityDao::class.java, SavedCityDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MyEventDao::class.java, MyEventDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(NiyamRecordDao::class.java, NiyamRecordDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecs(): Set<Class<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: HashSet<Class<out AutoMigrationSpec>> =
        HashSet<Class<out AutoMigrationSpec>>()
    return _autoMigrationSpecsSet
  }

  public override
      fun getAutoMigrations(autoMigrationSpecs: Map<Class<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = ArrayList<Migration>()
    return _autoMigrations
  }

  public override fun savedCityDao(): SavedCityDao = _savedCityDao.value

  public override fun myEventDao(): MyEventDao = _myEventDao.value

  public override fun niyamRecordDao(): NiyamRecordDao = _niyamRecordDao.value
}
