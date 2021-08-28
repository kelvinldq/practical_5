package my.marcusneo.practical_5

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ContactDao {
    @Insert
    suspend fun insert(contacts: Contact)

    @Delete
    suspend fun delete(contacts: Contact)

    @Update
    suspend fun update(contacts: Contact)

    @Query("SELECT * FROM contact ORDER BY name ASC")
    fun getAllContact(): LiveData<List<Contact>>


}