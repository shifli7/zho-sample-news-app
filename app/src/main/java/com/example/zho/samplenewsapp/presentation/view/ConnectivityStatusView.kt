package com.example.zho.samplenewsapp.presentation.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.zho.samplenewsapp.BuildConfig
import com.example.zho.samplenewsapp.R
import com.example.zho.samplenewsapp.presentation.MainViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


@SuppressLint("RememberReturnType")
@Composable
fun ConnectivityStatusView(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: MainViewModel?
) {
    val connectivityObserver = remember {
        ConnectivityObserver(context)
    }

    val isConnected by connectivityObserver.observerConnectivity()
        .collectAsState(false)

//    AnimatedVisibility(
//        visible = !isConnected,
//        enter = slideInVertically(initialOffsetY = { -it }),
//        exit = slideOutVertically(targetOffsetY = { -it })
//    )

    if (!isConnected){
        AssistChip(
            onClick = {},
            label = {
                Text("Network Connection Lost")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.wifi_off_24),
                    contentDescription = "Wifi Off",
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            },
            modifier = modifier
                .padding(top = 30.dp)
        )

        viewModel?.setSnackBarStatus("No Connection!, You may get Cached News Feeds")

    } else {
        viewModel?.setSnackBarStatus(null)
    }
}

class ConnectivityObserver(private val context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observerConnectivity(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(false)
            }
        }

        val request = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}
