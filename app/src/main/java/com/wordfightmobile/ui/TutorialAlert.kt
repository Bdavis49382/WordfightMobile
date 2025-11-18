package com.wordfightmobile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordfightmobile.R
import com.wordfightmobile.viewModels.TutorialViewModel

@Composable
fun TutorialAlert() {
    val viewModel: TutorialViewModel = viewModel()
    Dialog(onDismissRequest = {viewModel.close()}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (viewModel.current.value?.img?.isNotBlank() == true)650.dp else 350.dp)
                .padding(0.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(textAlign = TextAlign.Center, fontSize = 25.sp,
                    text = viewModel.current.value?.title ?: "Loading...")
                Text(textAlign = TextAlign.Center,
                    text = viewModel.current.value?.text ?: "Loading...")
                if (viewModel.current.value?.img?.isNotBlank() == true) {
                    Image(painter = painterResource(viewModel.slideImages[viewModel.current.value?.img]?: 0), contentDescription = null,
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)))
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    Button({
                        viewModel.prevSlide()
                    }, modifier = Modifier.padding(top = 20.dp, end = 10.dp)) {
                        Icon(painter = painterResource(R.drawable.baseline_keyboard_arrow_left_24), contentDescription = "previous slide")
                    }
                    Button({
                        viewModel.nextSlide()
                    }, modifier = Modifier.padding(top = 20.dp, end = 10.dp)) {
                        Icon(painter = painterResource(R.drawable.baseline_keyboard_arrow_right_24), contentDescription = "next slide")
                    }
                }
            }

        }
    }
}