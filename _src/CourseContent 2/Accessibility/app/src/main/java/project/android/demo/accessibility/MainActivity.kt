package project.android.demo.accessibility

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var index = 0
    private val names = arrayListOf("Tim", "Daniel", "Philipp")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn1.setOnClickListener {
            tvName.text = names[index % names.size]
            index++
        }

        btn2.setOnClickListener {
            tvAge.text = "$index"
            index++
        }
    }
}
