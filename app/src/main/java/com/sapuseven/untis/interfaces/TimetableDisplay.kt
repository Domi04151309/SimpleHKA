package com.sapuseven.untis.interfaces

import com.sapuseven.untis.data.timetable.TimegridItem
import com.sapuseven.untis.models.untis.UntisDate

interface TimetableDisplay {
	fun addTimetableItems(items: List<TimegridItem>, timestamp: Long)

	fun onTimetableLoadingError(requestId: Int, code: Int?, message: String?)
}
