package Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val password: String,
    val email: String,
    val lastName: String,
    val firstName: String,
    val joinDate : String = getCurrentDate()
)

fun getCurrentDate(): String {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return formatter.format(java.util.Date())
}
