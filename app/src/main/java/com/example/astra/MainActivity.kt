package com.example.astra

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astra.R
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StartScreen(onNextClick = {})
        }
    }
}

val Playfair = FontFamily(
    Font(R.font.playfair_display_regular, FontWeight.Normal)
)

@Composable
fun StartScreen(onNextClick: () -> Unit) {

    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("Выберите дату") }
    var time by remember { mutableStateOf("Выберите время") }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.bgbg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Добро пожаловать!",
                fontSize = 36.sp,
                fontFamily = Playfair,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.all { c -> c.isLetter() || c.isWhitespace() }) {
                        name = it
                    }
                },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = date,
                onValueChange = {},
                readOnly = true,
                label = { Text("Дата рождения") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val cal = Calendar.getInstance()

                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                date = "$day.${month + 1}.$year"
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = time,
                onValueChange = {},
                readOnly = true,
                label = { Text("Время рождения") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val cal = Calendar.getInstance()

                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                time = "%02d:%02d".format(hour, minute)
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                        ).show()
                    }
            )

            Spacer(modifier = Modifier.height(20.dp))


            IconButton(
                onClick = onNextClick,
                modifier = Modifier.size(70.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_circle_right_24),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}