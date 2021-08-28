package my.marcusneo.practical_5

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import my.marcusneo.practical_5.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val contactViewModel: ContactViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        val contactArray: ArrayList<Contact> = contactList
//        val adapter = context?.let { ArrayAdapter<Contact>(it, android.R.layout.simple_list_item_1, contactArray) }
//        val listView: ListView = binding.lvRecords
//        listView.adapter = adapter
//
//        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
//                // Do something in response to the click
//            Toast.makeText(context, contactArray[position].toString(), Toast.LENGTH_SHORT).show()
//        }

//        if(contactViewModel.contactList.size > 0){
//            binding.tvRecordAmt.text = "No of record: ${contactViewModel.contactList.size}"
//        } else {
//            binding.tvRecordAmt.text = "No record"
//        }


        //Create an instance of custom adapter
        val contactAdapter: ContactAdapter = ContactAdapter()
        //contactAdapter.setContact(contactViewModel.contactList)


        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        contactViewModel.contactList.observe(
            viewLifecycleOwner, Observer {
                if(it.isEmpty()){
                    Toast.makeText(context, "No record", Toast.LENGTH_SHORT).show()
                } else {
                    contactAdapter.setContact(it)
                }
            }
        )

        binding.rvRecords.apply {
            adapter = contactAdapter
        }

        //binding.rvRecords.adapter = contactAdapter
        binding.tvRecordAmt.text = contactAdapter.itemCount.toString()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}