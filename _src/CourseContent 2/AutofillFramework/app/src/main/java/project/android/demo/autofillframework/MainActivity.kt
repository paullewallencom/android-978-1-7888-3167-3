package project.android.demo.autofillframework

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.view.autofill.AutofillManager
import android.support.v4.content.ContextCompat.getSystemService




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button.setOnClickListener {
            val afm = getSystemService(AutofillManager::class.java)
            afm?.requestAutofill(editText)
        }
    }
}
