package my.marcusneo.practical_5

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class ContactRepository (private val contactDao: ContactDao){
    //A cache copy of data from Room
    val allContact: LiveData<List<Contact>> = contactDao.getAllContact()

    @Suppress("RedundantSuspentModifier")
    @WorkerThread
    suspend fun insert(contact: Contact){
        contactDao.insert(contact)
    }

    @WorkerThread
    suspend fun delete(contact: Contact){
        contactDao.delete(contact)
    }

}