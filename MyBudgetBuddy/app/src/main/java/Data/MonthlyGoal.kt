package Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="monthly_goal")
data class MonthlyGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val month: String,
    val minGoal: Double,
    val maxGoal:Double
)
