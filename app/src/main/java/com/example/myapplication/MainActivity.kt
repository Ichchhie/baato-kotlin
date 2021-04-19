package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BaatoReverse(this)
                .setLatLon(LatLon(27.67444444, 85.28047222))
                .setAccessToken("your-token")
                .setRadius(2)
                .withListener(object : BaatoReverse.BaatoReverseRequestListener {
                    override fun onSuccess(places: PlaceAPIResponse?) {
                        Log.d("TAG", "onSuccess: ")
                    }

                    override fun onFailed(error: Throwable?) {
                        Log.d("TAG", "onFailed: ")
                    }
                })
                .doRequest()
    }
}