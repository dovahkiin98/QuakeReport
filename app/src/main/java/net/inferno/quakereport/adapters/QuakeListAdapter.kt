package net.inferno.quakereport.adapters

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import net.inferno.quakereport.R
import net.inferno.quakereport.data.EarthQuake
import java.text.DecimalFormat

class QuakeListAdapter(context: Context, list: List<EarthQuake>) : ArrayAdapter<EarthQuake>(context, R.layout.quake_item, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null)
            view = LayoutInflater.from(parent.context).inflate(R.layout.quake_item, parent, false)

        val magText = view?.findViewById<TextView>(R.id.magText)
        val titleText = view?.findViewById<TextView>(R.id.titleText)
        val placeText = view?.findViewById<TextView>(R.id.placeText)
        val dateText = view?.findViewById<TextView>(R.id.dateText)
        val timeText = view?.findViewById<TextView>(R.id.timeText)

        val quake = getItem(position)

        magText?.text = DecimalFormat("0.0").format(quake.mag)
        titleText?.text = quake.titleShort
        placeText?.text = quake.placeShort
        dateText?.text = DateFormat.format("MMM dd yyyy", quake.time)
        timeText?.text = DateFormat.format("hh:mm a", quake.time)

        val colorId = when (Math.floor(quake.mag).toInt()) {
            0, 1 -> R.color.magnitude1
            2 -> R.color.magnitude2
            3 -> R.color.magnitude3
            4 -> R.color.magnitude4
            5 -> R.color.magnitude5
            6 -> R.color.magnitude6
            7 -> R.color.magnitude7
            8 -> R.color.magnitude8
            9 -> R.color.magnitude9
            else -> R.color.magnitude10plus
        }
        val color = ContextCompat.getColor(context, colorId)
        (magText?.background as GradientDrawable).setColor(color)

        return view!!
    }
}