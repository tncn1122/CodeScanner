package com.example.codescanner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator


class MainActivity : AppCompatActivity() {
    private lateinit var scanBtn : Button
    private lateinit var mainText : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scanBtn = findViewById<Button>(R.id.scanBtn)

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
                mainText = findViewById<TextView>(R.id.TextV)
                mainText.text = value
            } else {
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}