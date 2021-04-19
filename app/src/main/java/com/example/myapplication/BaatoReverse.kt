package com.example.myapplication

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class BaatoReverse(private val context: Context) {
    private var baatoReverseRequestListener: BaatoReverseRequestListener? = null
    private var accessToken: String? = null
    private var apiVersion = "1"
    private var apiBaseUrl = "https://api.baato.io/api/v1/"
    private var radius = 0
    private var limit = 0
    private var latLon: LatLon? = null
    private var placeAPIResponseCall: Call<PlaceAPIResponse>? = null

    interface BaatoReverseRequestListener {
        /**
         * onSuccess method called after it is successful
         * onFailed method called if it can't search places
         */
        fun onSuccess(places: PlaceAPIResponse?)
        fun onFailed(error: Throwable?)
    }

    /**
     * Set the accessToken.
     */
    fun setAccessToken(accessToken: String): BaatoReverse {
        this.accessToken = accessToken
        return this
    }

    /**
     * Set the apiVersion. By default it takes version "1"
     */
    fun setAPIVersion(apiVersion: String): BaatoReverse {
        this.apiVersion = apiVersion
        return this
    }

    /**
     * Set the apiBaseURL.
     */
    fun setAPIBaseURL(apiBaseURL: String): BaatoReverse {
        apiBaseUrl = apiBaseURL
        return this
    }

    /**
     * Set the geocode to search.
     */
    fun setLatLon(latLon: LatLon): BaatoReverse {
        this.latLon = latLon
        return this
    }

    /**
     * Set the radius to search.
     */
    fun setRadius(radius: Int): BaatoReverse {
        this.radius = radius
        return this
    }

    /**
     * Set the limit to search.
     */
    fun setLimit(limit: Int): BaatoReverse {
        this.limit = limit
        return this
    }

    /**
     * Method to set the UpdateListener for the AppUpdaterUtils actions
     *
     * @param baatoReverseRequestListener the listener to be notified
     * @return this
     */
    fun withListener(baatoReverseRequestListener: BaatoReverseRequestListener?): BaatoReverse {
        this.baatoReverseRequestListener = baatoReverseRequestListener
        return this
    }

    fun doRequest() {
        val logging = HttpLoggingInterceptor()
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        httpClient.addInterceptor(logging) // <-- this is the important line!

        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
        val service = retrofit.create(BaatoAPI::class.java)
        val call = service.performReverseGeoCode(giveMeQueryFilter())

        call.enqueue(object : Callback<PlaceAPIResponse?> {
            override fun onResponse(
                call: Call<PlaceAPIResponse?>, response: Response<PlaceAPIResponse?>
            ) {
                if (response.isSuccessful() && response.body() != null) baatoReverseRequestListener!!.onSuccess(
                    response.body()
                ) else {
                    try {
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<PlaceAPIResponse?>, throwable: Throwable) {
                baatoReverseRequestListener!!.onFailed(throwable)
            }
        })
    }

    fun cancelRequest() {
        placeAPIResponseCall!!.cancel()
    }

    private fun giveMeQueryFilter(): Map<String, String> {
        val queryMap: MutableMap<String, String> = HashMap()
        //compulsory
        queryMap["key"] = accessToken!!
        if (limit != 0) queryMap["limit"] = limit.toString() + ""
        queryMap["lat"] = latLon!!.lat.toString() + ""
        queryMap["lon"] = latLon!!.lon.toString() + ""

        //optional
        if (radius != 0) queryMap["radius"] = radius.toString() + ""
        Log.d(TAG, "giveMeQueryFilter: $queryMap")
        return queryMap
    }

    companion object {
        private const val TAG = "BaatoReverseGeoCode"
    }
}