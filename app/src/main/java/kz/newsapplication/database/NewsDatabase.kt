package kz.newsapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kz.newsapplication.BuildConfig
import kz.newsapplication.model.News
import kz.newsapplication.model.converters.SourceTypeConverter

@Database(entities = [News::class], version = 1)
@TypeConverters(SourceTypeConverter::class)
abstract class NewsDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "database"

        fun getDatabase(context: Context): NewsDatabase {
            return Room.databaseBuilder(context, NewsDatabase::class.java, DATABASE_NAME)
                .apply {
                    if (BuildConfig.DEBUG) {
                        fallbackToDestructiveMigration()
                    }
                    allowMainThreadQueries()
                }
                .build()
        }
    }

    abstract fun newsDao(): DaoNews
}
