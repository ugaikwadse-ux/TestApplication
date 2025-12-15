package com.quicksoft.testapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.quicksoft.testapp.R
import com.quicksoft.testapp.SiginActivity
import com.quicksoft.testapp.model.SideItems

class SideAdapter(
    private val items: List<SideItems>
) : RecyclerView.Adapter<SideAdapter.FollowerViewHolder>() {

    inner class FollowerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePhoto: AppCompatImageView =
            itemView.findViewById(R.id.profilePhoto)
        val nameText: TextView =
            itemView.findViewById(R.id.nameText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sidebar, parent, false)
        return FollowerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowerViewHolder, position: Int) {
        val item = items[position]

        holder.nameText.text = item.name
        holder.profilePhoto.setImageResource(item.profilePhoto)

        if (position == items.size - 1) {
            holder.itemView.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val context = holder.itemView.context
                val intent = Intent(context, SiginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = items.size
}
