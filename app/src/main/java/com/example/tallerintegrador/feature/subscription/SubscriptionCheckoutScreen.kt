package com.example.tallerintegrador.feature.subscription

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionCheckoutScreen(
    navController: NavController,
    encodedUrl: String,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val decodedUrl = remember(encodedUrl) { Uri.decode(encodedUrl) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pago de suscripción") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearError()
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cerrar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val target = request?.url ?: return false
                                if (target.scheme == "cinemaaguilas" && target.host == "stripe") {
                                    val status = target.lastPathSegment ?: "cancel"
                                    val sessionId = target.getQueryParameter("session_id")
                                    viewModel.handleCheckoutCallback(status, sessionId)
                                    navController.popBackStack()
                                    return true
                                }
                                if (target.scheme == "netflixapp" && target.host == "stripe") {
                                    val status = target.pathSegments.lastOrNull() ?: "cancel"
                                    val sessionId = target.getQueryParameter("session_id")
                                    viewModel.handleCheckoutCallback(status, sessionId)
                                    navController.popBackStack()
                                    return true
                                }
                                return target.scheme != "http" && target.scheme != "https"
                            }
                        }
                        loadUrl(decodedUrl)
                    }
                },
                update = { it.loadUrl(decodedUrl) }
            )
        }
    }
}
