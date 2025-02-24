package project.android.demo.text

import android.os.LocaleList
import android.support.v7.widget.RecyclerView
import android.text.PrecomputedText
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.textclassifier.TextClassificationManager
import android.widget.Magnifier
import android.widget.TextView
import org.w3c.dom.Text
import java.lang.ref.WeakReference
import java.util.concurrent.ScheduledThreadPoolExecutor

class RecyclerAdapter(private val data: ArrayList<String>) : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    private val background = ScheduledThreadPoolExecutor(5)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val params = holder.textView.textMetricsParams
        val ref = WeakReference<TextView>(holder.textView)

        background.submit {
            val tv = ref.get()
            val precomputedText = PrecomputedText.create(data[position], params)
            // return to UI thread
            tv?.post {
                tv.text = precomputedText
            }
        }

        val magnifier = Magnifier(holder.itemView)

        holder.itemView.setOnTouchListener { v, event ->

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val pos = IntArray(2)
                    v.getLocationOnScreen(pos)
                    magnifier.show(event.rawX - pos[0], event.rawY - pos[1])
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    magnifier.dismiss()
                }
            }
            true
        }

        holder.itemView.setOnClickListener {

            val manager = holder.itemView.context.getSystemService(TextClassificationManager::class.java)

            background.submit {
                val classification = manager?.textClassifier?.classifyText(data[position], 0, data[position].length, LocaleList.getDefault())
                println(classification)
            }
        }
    }

    // Return the size of your data (invoked by the layout manager)
    override fun getItemCount() = data.size

    // provide a reference to the views of the item layout
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
    }
}