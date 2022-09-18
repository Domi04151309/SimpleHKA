package com.sapuseven.untis.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sapuseven.untis.R
import com.sapuseven.untis.activities.AddStationActivity
import com.sapuseven.untis.adapters.MessageAdapter
import com.sapuseven.untis.data.lists.ListItem
import com.sapuseven.untis.helpers.strings.StringLoader
import com.sapuseven.untis.interfaces.StringDisplay
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference


class StationsFragment : Fragment(), StringDisplay {
	private val stationList = arrayListOf<ListItem>()
	private val stationAdapter = MessageAdapter(stationList)
	private var stationsLoading = true
	private val keyMap: MutableMap<String, Pair<Double, Double>> = mutableMapOf()
	private lateinit var stringLoader: StringLoader
	private lateinit var recyclerview: RecyclerView
	private lateinit var swiperefreshlayout: SwipeRefreshLayout

	companion object {
		private const val API_URL: String = "https://www.iwi.hs-karlsruhe.de/iwii/REST"
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val root = inflater.inflate(
			R.layout.fragment_stations,
			container,
			false
		)

		stringLoader = StringLoader(WeakReference(context), this, "${API_URL}/buildings/v2/all")
		recyclerview = root.findViewById(R.id.recyclerview_infocenter)
		swiperefreshlayout = root.findViewById(R.id.swiperefreshlayout_infocenter)

		recyclerview.layoutManager = LinearLayoutManager(context)
		recyclerview.adapter = stationAdapter
		swiperefreshlayout.isRefreshing = stationsLoading
		swiperefreshlayout.setOnRefreshListener { refreshStations(StringLoader.FLAG_LOAD_SERVER) }

		refreshStations(StringLoader.FLAG_LOAD_CACHE)

		stationAdapter.onClickListener = View.OnClickListener {
			val key = it.findViewById<TextView>(R.id.textview_itemmessage_subject).text.toString()
			if (key.isNotEmpty()) {
				val mapIntent = Intent(
					Intent.ACTION_VIEW, Uri.parse(
						"geo:0,0?q=${keyMap[key]?.first},${keyMap[key]?.second}"
					)
				)
				mapIntent.setPackage("com.google.android.apps.maps")
				startActivity(mapIntent)
			}
		}

		root.findViewById<FloatingActionButton>(R.id.button_stations_add).setOnClickListener {
			startActivity(Intent(context, AddStationActivity::class.java))
		}

		return root
	}

	private fun refreshStations(flags: Int) {
		stationsLoading = true
		stringLoader.load(flags)
	}

	override fun onStringLoaded(string: String) {
		stationList.clear()
		val json = JSONArray(string)
		var departments: JSONArray
		var currentBuilding: JSONObject
		var currentDepartment: String
		for (i in 0 until json.length()) {
			currentBuilding = json.getJSONObject(i)
			departments = currentBuilding.optJSONArray("departments") ?: JSONArray()
			if (departments.length() > 0) stationList.add(
				ListItem(
					"",
					currentBuilding.optString("name")
				)
			)
			for (j in 0 until departments.length()) {
				currentDepartment = departments.getJSONObject(j).optString("name")
				stationList.add(
					ListItem(
						currentDepartment,
						""
					)
				)
				keyMap[currentDepartment] = Pair(
					currentBuilding.optDouble("latitude"),
					currentBuilding.optDouble("longitude")
				)
			}
		}
		stationAdapter.notifyDataSetChanged()
		stationsLoading = false
		swiperefreshlayout.isRefreshing = false
	}

	override fun onStringLoadingError(code: Int) {
		when (code) {
			StringLoader.CODE_CACHE_MISSING -> stringLoader.repeat(
				StringLoader.FLAG_LOAD_SERVER
			)
			else -> {
				Toast.makeText(
					context,
					R.string.errors_failed_loading_from_server_message,
					Toast.LENGTH_LONG
				).show()
				stationsLoading = false
				swiperefreshlayout.isRefreshing = false
			}
		}
	}
}
