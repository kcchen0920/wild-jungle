package com.game.wildcatcher.data

import android.content.Context
import androidx.room.*
import com.game.wildcatcher.data.dao.*
import com.game.wildcatcher.data.model.*

class WildTypeConverters {
    @TypeConverter
    fun fromConservationGrade(value: ConservationGrade): String = value.name

    @TypeConverter
    fun toConservationGrade(value: String): ConservationGrade = ConservationGrade.valueOf(value)

    @TypeConverter
    fun fromHabitatType(value: HabitatType): String = value.name

    @TypeConverter
    fun toHabitatType(value: String): HabitatType = HabitatType.valueOf(value)

    @TypeConverter
    fun fromActivityCycle(value: ActivityCycle): String = value.name

    @TypeConverter
    fun toActivityCycle(value: String): ActivityCycle = ActivityCycle.valueOf(value)
}

@Database(
    entities = [
        AnimalEntity::class,
        PlayerProfile::class,
        ShelterZone::class,
        EcoQuiz::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(WildTypeConverters::class)
abstract class WildDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
    abstract fun playerDao(): PlayerDao
    abstract fun shelterDao(): ShelterDao
    abstract fun quizDao(): QuizDao

    companion object {
        @Volatile
        private var INSTANCE: WildDatabase? = null

        fun getDatabase(context: Context): WildDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WildDatabase::class.java,
                    "wild_catcher.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
