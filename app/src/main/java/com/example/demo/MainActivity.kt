package com.example.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demo.ui.theme.DemoTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DemoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    StateLabScreen()
                }
            }
        }
    }
}

@Composable
fun StateLabScreen(
    viewModel: RandomNumberViewModel = viewModel()
) {
    // A: remember - chỉ giữ state qua recomposition.
    var numberA by remember { mutableIntStateOf(0) }

    // B: rememberSaveable - lưu vào Bundle, sống qua xoay màn hình và process death.
    var numberB by rememberSaveable { mutableIntStateOf(0) }

    // C: ViewModel + SavedStateHandle - sống qua xoay màn hình và process death.
    val numberC by viewModel.savedNumber.collectAsStateWithLifecycle()

    // D: ViewModel thường - sống qua xoay màn hình, mất khi process death.
    val numberD by viewModel.plainNumber.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Saving and Restoring State",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        StateCard(
            title = "A - remember",
            value = numberA,
            buttonText = "Generate A",
            onGenerate = { numberA = Random.nextInt(1, 1000) }
        )

        StateCard(
            title = "B - rememberSaveable",
            value = numberB,
            buttonText = "Generate B",
            onGenerate = { numberB = Random.nextInt(1, 1000) }
        )

        StateCard(
            title = "C - ViewModel + SavedStateHandle",
            value = numberC,
            buttonText = "Generate C",
            onGenerate = { viewModel.generateSavedNumber() }
        )

        StateCard(
            title = "D - ViewModel (không SavedStateHandle)",
            value = numberD,
            buttonText = "Generate D",
            onGenerate = { viewModel.generatePlainNumber() }
        )
    }
}

@Composable
fun StateCard(
    title: String,
    value: Int,
    buttonText: String,
    onGenerate: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 18.sp)
            Text("Random Number: $value", fontSize = 20.sp)
            Button(onClick = onGenerate) {
                Text(buttonText)
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
}