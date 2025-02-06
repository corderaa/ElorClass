package com.example.elorclass.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.elorclass.R

class ScheduleItem(
    var scheduleHour: String,
    var scheduleTask: String
)

class ScheduleItemArrayAdapter(
    context: Context?,
    resource: Int,
    objects: List<ScheduleItem>?
): ArrayAdapter<ScheduleItem>(context!!, resource, objects!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(this.context)
            .inflate(R.layout.schedule_item, parent, false)

        val task = view.findViewById<TextView>(R.id.taskbbddTextView)
        val hour = view.findViewById<TextView>(R.id.hourbbddTextView)

        getItem(position)?.let {
            task.text = it.scheduleTask
            hour.text = it.scheduleHour.toString()

        }
        return view
    }
}