package com.example.zho.samplenewsapp.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zho.samplenewsapp.R

@Composable
fun ShowNoDataView(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.baseline_forest_24),
    contentDescription: String? = "No Data Found",
    text: String = "No Data Found"
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 70.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painter,
            contentDescription = "",
            tint = Color.LightGray,
            modifier = Modifier
                .size(width = 180.dp, height = 180.dp)
                .padding(20.dp)
        )
        Text(
            text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
