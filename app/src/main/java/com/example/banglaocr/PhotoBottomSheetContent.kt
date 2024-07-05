package com.example.banglaocr


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun PhotoBottomSheetContent(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .height(500.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val bitmap by viewModel.bitmaps.collectAsState()
        val outputBitmap by viewModel.bitmapPlate.collectAsState()
        val text by viewModel.text.collectAsState()
        if (bitmap != null) {
            Text(text = "Input Image")
            Image(bitmap = bitmap!!.asImageBitmap(), contentDescription ="" , modifier = Modifier.heightIn(min = 100.dp, max = 150.dp))
            if(outputBitmap != null)
            {
                Text(text = "Output Image")
                Image(bitmap = outputBitmap!!.asImageBitmap(), contentDescription ="" , modifier = Modifier.heightIn(min = 100.dp, max = 150.dp), contentScale = ContentScale.FillBounds)
            }
            if(text.length<2)
            {
                Text(text = "Processing...")
            }
            else{
                Text(text = "License No : $text")
            }


        } else {
            Text("There is no photo to convert")
        }
    }
}