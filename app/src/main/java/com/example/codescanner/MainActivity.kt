package com.example.codescanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private var res : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        scanBtn.setOnClickListener {
            scanCode()
        }


    }

    private fun scanCode(){
        var integrator : IntentIntegrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            val value = result.contents
            if (value != null) {
                GlobalScope.launch(Dispatchers.IO) {
                    Log.d("baokiin",value)
                var formBody  = FormBody.Builder()
                    .add("MSSV" , value)
                    .build()
                var request = Request.Builder()
                    .url("http://checkin-ptit.herokuapp.com/api/checkin")
                    .post(formBody)
                    .build()
                var client = OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()
                var response = client.newCall(request).execute()

                    runOnUiThread {
                        textID.text = response.message()
                    }
                    scanCode()
                    }
            }
            else {
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



}