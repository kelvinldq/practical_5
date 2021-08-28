package my.marcusneo.practical_5

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.database.FirebaseDatabase
import my.marcusneo.practical_5.databinding.FragmentProfileBinding
import my.tarc.mycontact.WebDB
import org.json.JSONObject

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){
        binding.ivPicture.setImageURI(it)
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

        binding.btnSaveToFire.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val myReference = database.getReference("profile")
            val profileInstance = Profile(binding.editTextName.text.toString(), binding.etPhone.text.toString())
            myReference.child(profileInstance.phone.toString()).child("name").setValue(profileInstance.name)
            myReference.child(profileInstance.phone.toString()).child("phone").setValue(profileInstance.phone)
            binding.editTextName.text.clear()
            binding.etPhone.text.clear()
            binding.editTextName.clearFocus()
            binding.etPhone.clearFocus()

            val profile = Profile(name = binding.editTextName.text.toString(), phone = binding.etPhone.text.toString())
            val url = getString(R.string.url_server) + getString(R.string.url_add_profile)+ "?name=" + profile.name + "&phone=" + profile.phone
            addProfile(activity?.applicationContext!!, url)

            val sharedPreferences: SharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!
            with(sharedPreferences.edit()){
                putString("name", profile.name)
                putString("phone", profile.phone)
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
    }
}