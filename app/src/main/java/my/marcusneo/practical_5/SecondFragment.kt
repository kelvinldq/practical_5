package my.marcusneo.practical_5

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import my.marcusneo.practical_5.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val contactViewModel: ContactViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            if(binding.editTextPersonName.text.isEmpty()){
                binding.editTextPersonName.error = getString(R.string.errorMessage)
                return@setOnClickListener
            }
            if(binding.editTextPhone.text.isEmpty()){
                binding.editTextPhone.error = getString(R.string.errorMessage)
                return@setOnClickListener
            }

            //contactList.add(Contact(binding.editTextPersonName.text.toString(), binding.editTextPhone.text.toString()))
            binding.apply {
                contactViewModel.addContact(Contact(binding.editTextPersonName.text.toString(), binding.editTextPhone.text.toString()))
            }
            Snackbar.make(view,"Record Saved", Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}