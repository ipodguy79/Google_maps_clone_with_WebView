package com.app.hotspringsofbc

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError


class WebViewActivity : AppCompatActivity() {

    private lateinit var webView:WebView
    private val URL = "https://www.hotspringsofbc.ca"



    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        webView = findViewById(R.id.web)


        webView.apply {
            loadUrl(URL)
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true

        }
        val intent = intent
        val data: Uri? = intent.data
        if (data != null) {
            //Load the deep-link url:
            val url = intent.dataString
            webView.loadUrl(URL)
        } else{webView.loadUrl("https://hotspringsofbc.ca/")
        }

        mAdView = findViewById(R.id.adView2)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.


            }
        }

    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        }else {
            super.onBackPressed()
        }
    }
}