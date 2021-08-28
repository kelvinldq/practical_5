package my.marcusneo.practical_5

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class ContactViewModel(application: Application): AndroidViewModel(application) {

    //Livedata give use updated contact when they change
    var contactList : LiveData<List<Contact>>
    private val repository: ContactRepository

    init {
        Log.d("ViewModel", "Initialize")
        val contactDao = ContactDatabase.getDatabase(application).contactDao()
        repository = ContactRepository(contactDao)
        contactList = repository.allContact
    }

    fun addContact(contact: Contact) = viewModelScope.launch{
        //contactList.add(contact)
        repository.insert(contact)
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch{
        //contactList.remove(contact)
        repository.delete(contact)
    }

    fun uploadContact(){
        val firebase : FirebaseDatabase = FirebaseDatabase.getInstance()
        val myReference = firebase.getReference("profile")

        for (contact in contactList.value?.iterator()!!){
            myReference.child(contact.phone).child("phone").setValue(contact.phone)
            myReference.child(contact.phone).child("name").setValue(contact.name)
        }
    }

    override fun onCleared() {

        super.onCleared()
        Log.d("ViewModel","Cleared")
    }
}