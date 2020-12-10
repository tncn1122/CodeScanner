package com.example.codescanner

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
 
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    var check = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button.setOnClickListener {
            check = true;

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
        progressBar.visibility = View.VISIBLE
        txtId.visibility = View.INVISIBLE
        txtName.visibility = View.INVISIBLE
        txtmajor.visibility = View.INVISIBLE
        txtSTT.visibility = View.INVISIBLE
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            val value = result.contents
            if (value != null) {
                GlobalScope.launch(Dispatchers.IO) {
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
                        progressBar.visibility = View.INVISIBLE
                        txtId.visibility = View.VISIBLE
                        txtName.visibility = View.VISIBLE
                        txtmajor.visibility = View.VISIBLE
                        txtSTT.visibility = View.VISIBLE
                        val post = JSONObject(response.body().string())
                        if(post["status"].toString().equals("NOTFOUND")){
                            txtSTT.text = "Bạn không có trong sanh sách"
                            txtId.visibility = View.INVISIBLE
                            txtName.visibility = View.INVISIBLE
                            txtmajor.visibility = View.INVISIBLE
                        }
                        else{
                            txtId.text = post["mssv"].toString()
                            txtName.text = post["name"].toString()
                            txtmajor.text = post["major"].toString()

                            if(post["status"].toString().equals("LATE"))
                                txtSTT.text = "Điểm danh trễ"

                            else if(post["status"].toString().equals("SUCCESS"))
                                txtSTT.text = "Điểm danh thành công"
                            else if(post["status"].toString().equals("CHECKED"))
                                txtSTT.text = "Bạn đã điểm danh rồi"
                        }

                    }
                    //res.postValue(response.body().string())
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