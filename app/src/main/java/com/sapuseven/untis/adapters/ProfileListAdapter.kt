package com.sapuseven.untis.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sapuseven.untis.R
import com.sapuseven.untis.data.databases.LinkDatabase

class ProfileListAdapter(
	private val dataset: MutableList<LinkDatabase.Link>,
	private val onClickListener: View.OnClickListener,
	private val onLongClickListener: View.OnLongClickListener
) :
	RecyclerView.Adapter<ProfileListAdapter.ViewHolder>() {

	class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
		var tvName: TextView = itemView.findViewById(R.id.textview_profiles_name)
		var tvSchool: TextView = itemView.findViewById(R.id.textview_profiles_school)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val v = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_profiles, parent, false) as ConstraintLayout
		v.setOnClickListener(onClickListener)
		v.setOnLongClickListener(onLongClickListener)
		return ViewHolder(v)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = dataset[position]
		holder.tvName.text = item.id.toString()
		holder.tvSchool.text = item.iCalUrl.substring(item.iCalUrl.lastIndexOf('/') + 1)
	}

	override fun getItemCount() = dataset.size

	fun itemAt(position: Int) = dataset[position]
}
