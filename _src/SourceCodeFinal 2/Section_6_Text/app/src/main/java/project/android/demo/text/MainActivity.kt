package project.android.demo.text

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = ArrayList<String>()
        data.add("email@mydomain.com")
        data.add("+43 664 123123123")
        data.add("5th Avenue, New York")
        data.add("https://www.google.com")

        for (i in 0..100) {
            data.add("Item $i")
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerAdapter(data)
    }
}
