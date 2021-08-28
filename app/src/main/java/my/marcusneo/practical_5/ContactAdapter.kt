package my.marcusneo.practical_5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter  internal constructor(): RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    private var contactList = emptyList<Contact>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        //To hold each record
        val tvName: TextView = view.findViewById(R.id.tvContactName)
        val tvContactNum: TextView = view.findViewById(R.id.tvContactNum)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //To create a layout to hold each record
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Connect data to the layout
        holder.tvName.text = contactList[position].name
        holder.tvContactNum.text = contactList[position].phone
        holder.itemView.setOnClickListener{
            Toast.makeText(it.context, "Contact Name:" + contactList[position].name, Toast.LENGTH_SHORT).show()
        }
    }

    internal fun setContact(contact: List<Contact>){
        this.contactList = contact
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return contactList.count()
    }
}