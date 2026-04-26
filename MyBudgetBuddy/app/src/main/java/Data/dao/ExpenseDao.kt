package Data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import Data.Expense

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE strftime('y', date) = :year AND strftime('m', date) = :month ORDER BY date ASC")
    suspend fun GetExpensesForMonth(year: String, month: String): List<Expense>
}