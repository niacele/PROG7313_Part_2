package Data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import Data.Expense

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT SUM(amount) FROM expenses WHERE date LIKE :month || '%'")
    suspend fun getTotalForMonth(month: String): Double?

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getExpensesBetweenDates(startDate: String, endDate: String): List<Expense>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun GetExpenseById(id: Int): Expense
}