package Data.database

import Data.dao.ExpenseDao
import Data.dao.UserDao
import Data.dao.MonthlyDao
import android.content.Context
import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.Room
import Data.Expense
import Data.User
import Data.MonthlyGoal

@Database(
    entities = [User::class, MonthlyGoal::class, Expense::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun monthlyDao(): MonthlyDao
    abstract fun expenseDao(): ExpenseDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}