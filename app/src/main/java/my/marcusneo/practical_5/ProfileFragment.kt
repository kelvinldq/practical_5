package my.marcusneo.practical_5

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.marcusneo.practical_5.databinding.FragmentProfileBinding
import my.tarc.mycontact.WebDB
import org.json.JSONObject
import java.io.*

class ProfileFragment : Fragment() {

    private lateinit var filePath:Uri
    private var _binding: FragmentProfileBinding? = null
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){
        binding.ivPicture.setImageURI(it)
        filePath = it
    }
    private lateinit var preferences: SharedPreferences

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_profile).isVisible = false
        menu.findItem(R.id.action_settings).isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivPicture.setOnClickListener {
            getContent.launch("image/*")
        }

        preferences = view.context.getSharedPreferences(PROFILE_PREFERENCES, Context.MODE_PRIVATE)
        val name = preferences.getString(PROFILE_NAME, "")
        binding.editTextName.setText(name.toString())
        val phone = preferences.getString(PROFILE_PHONE,"")
        binding.etPhone.setText(phone.toString())
        val picture = preferences.getString(PROFILE_PICTURE,"")
        if(picture.isNullOrBlank()){
            binding.ivPicture.setImageResource(R.drawable.kelvinpic)
        }else{
            try{
                val dir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val file = File(dir,phone.toString()+".jpg")
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.ivPicture.setImageBitmap(bitmap)
            }catch (Exception : IOException){
                Log.d("Profile Picture",Exception.message.toString())
            }
        }

        binding.btnSaveToFire.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val myReference = database.getReference("profile")
            val profile = Profile(name = binding.editTextName.text.toString(), phone = binding.etPhone.text.toString(), picture = phone.toString()+".jpg")
            myReference.child(profile.phone.toString()).child("name").setValue(profile.name)
            myReference.child(profile.phone.toString()).child("phone").setValue(profile.phone)

            saveProfilePicture(profile.phone.toString()+".jpg")
            uploadProfilePicture(profile.phone.toString()+".jpg")
            val url = getString(R.string.url_server) + getString(R.string.url_add_profile)+ "?name=" + profile.name + "&phone=" + profile.phone
            addProfile(activity?.applicationContext!!, url)

            val sharedPreferences: SharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!
            val dir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            val file = File(dir,profile.phone+".jpg")
            with(sharedPreferences.edit()){
                putString(PROFILE_NAME, profile.name)
                putString(PROFILE_PHONE, profile.phone)
                putString(PROFILE_PICTURE,file.toString())

                apply()
            }
        }

        binding.btnIG.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_ig)))
            if(intent.resolveActivity(context?.packageManager!!) != null){
                startActivity(intent)
            }
        }
    }

    private fun uploadProfilePicture(fileName: String) {
        val storage = Firebase.storage("gs://practical5-641b4.appspot.com")
        val storageReference = storage.reference.child("picture").child(fileName)
        binding.progressBar.visibility = View.VISIBLE
        storageReference.putFile(filePath)
            .addOnSuccessListener {
                Toast.makeText(context,"Picture uploaded.",Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener{
                Toast.makeText(context,"Error:${it.message}",Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
            .addOnProgressListener {
                var progress: Double = (100.0 * it.bytesTransferred) / it.totalByteCount
                binding.progressBar.progress = progress.toInt()
            }
    }

    private fun saveProfilePicture(fileName:String) {
        val dir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        if(!dir.exists()){
            dir.mkdir()
        }
        val drawable = binding.ivPicture.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val file = File(dir,fileName)
        val outputStream:OutputStream
        try{
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
            outputStream.flush()
            outputStream.close()
            Log.d("File save successful",fileName)
        }catch (Exception:FileNotFoundException){
            Log.d("Save File",Exception.message.toString())
        }
    }

    fun addProfile(context: Context, url: String) {
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Process the JSON
                try {
                    if (response != null) {
                        val strResponse = response.toString()
                        val jsonResponse = JSONObject(strResponse)
                        val success: String = jsonResponse.get("success").toString()
                        val id: String = jsonResponse.get("id").toString()

                        Log.d("ContactRepository", "Response:$success")
                        Log.d("ContactRepository", "Response:$id")
                        Integer.parseInt(id)
                    }
                } catch (e: Exception) {
                    Log.d("ContactRepository", "Response: %s".format(e.message.toString()))
                }
            },
            { error ->
                Log.d("ContactRepository", "Response: %s".format(error.message.toString()))
            }
        )

        //Volley request policy, only one time request
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            0, //no retry
            1f
        )

        // Access the RequestQueue through your singleton class.
        WebDB.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }


    override fun onPause() {
        super.onPause()
        with(preferences.edit()){
            val name = binding.editTextName.text.toString()
            putString(PROFILE_NAME, name)
            apply()
        }
    }

    companion object {
        const val PROFILE_PREFERENCES = "Preferences"
        const val PROFILE_NAME = "Name"
        const val PROFILE_PHONE = "Phone"
        const val PROFILE_PICTURE = "Picture"
    }
}