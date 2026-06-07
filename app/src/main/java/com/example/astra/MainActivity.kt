package com.example.astra

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AstraApp()
        }
    }
}

val Playfair = FontFamily(
    Font(R.font.playfair_display_regular, FontWeight.Normal)
)

@Composable
fun AstraApp() {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var started by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as ComponentActivity

    val goToPage: (Int) -> Unit = { pageIndex: Int ->
        scope.launch {
            pagerState.animateScrollToPage(pageIndex)
        }
    }

    if (!started) {
        StartScreen(
            onNextClick = { started = true },
            onNameChange = { name = it },
            onDateChange = { date = it },
            onTimeChange = { time = it },
            onCityChange = { city = it }
        )
    } else {
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> MoonPage(
                    selected = pagerState.currentPage,
                    onTabClick = { pageIndex: Int ->
                        scope.launch { pagerState.animateScrollToPage(pageIndex) }
                    }
                )
                1 -> AdvicePage(
                    selected = pagerState.currentPage,
                    onTabClick = { pageIndex: Int ->
                        scope.launch { pagerState.animateScrollToPage(pageIndex) }
                    }
                )
                2 -> ProfilePage(
                    selected = pagerState.currentPage,
                    name = name,
                    date = date,
                    time = time,
                    city = city,
                    onExitClick = {
                        activity.finish()
                    },
                    onEditClick = {
                        started = false
                    },
                    onTabClick = { pageIndex ->
                        scope.launch {
                            pagerState.animateScrollToPage(pageIndex)
                        }
                    }
                )
            }
        }
    }
}
@Composable
fun StartScreen(
    onNextClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onCityChange: (String) -> Unit
) {
    val whiteFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,

        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White,

        focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f),

        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,

        disabledTextColor = Color.White,
        disabledBorderColor = Color.White
    )

    var name by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }

    var date by rememberSaveable { mutableStateOf("") }
    var time by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(R.drawable.bgbg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(34.dp))
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
                    name = it
                    onNameChange(it)
                },
                placeholder = {
                    Text("Введите имя")
                },
                colors = whiteFieldColors,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                        val cal = Calendar.getInstance()

                        DatePickerDialog(
                            context,
                            { _, year, month, day ->

                                date = "%02d.%02d.%04d".format(
                                    day,
                                    month + 1,
                                    year
                                )

                                onDateChange(date)
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    placeholder = {
                        Text(
                            "Дата рождения",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    },
                    colors = whiteFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            }


            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                        val cal = Calendar.getInstance()

                        TimePickerDialog(
                            context,
                            { _, hour, minute ->

                                time = "%02d:%02d".format(
                                    hour,
                                    minute
                                )

                                onTimeChange(time)
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                        ).show()
                    }
            ) {
                OutlinedTextField(
                    value = time,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    placeholder = {
                        Text(
                            "Время рождения",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    },
                    colors = whiteFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            }


            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = city,
                onValueChange = {
                    city = it
                    onCityChange(it)
                },
                placeholder = {
                    Text("Введите город")
                },
                colors = whiteFieldColors,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            IconButton(
                onClick = onNextClick,
                modifier = Modifier.size(72.dp)
            ) {

                Icon(
                    painter = painterResource(
                        R.drawable.baseline_arrow_circle_right_24
                    ),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
@Composable
fun MoonPage(
    selected: Int,
    onTabClick: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.moon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            BottomBar(selected = selected, onTabClick = onTabClick)
        }
    }
}

@Composable
fun AdvicePage(
    selected: Int,
    onTabClick: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.advice),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            AdviceCard(
                title = "Совет от луны",
                text = "заглушка текста\nзаглушка текста",
                icon = R.drawable.cat
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AdviceMiniCard(
                    title = "Гороскоп дня",
                    icon = R.drawable.cat,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(20.dp))
                AdviceMiniCard(
                    title = "Матрица дня",
                    icon = R.drawable.woman,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))  // <-- добавили отступ снизу
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            BottomBar(selected = selected, onTabClick = onTabClick)
        }
    }
}

@Composable
fun ProfilePage(
    selected: Int,
    name: String,
    date: String,
    time: String,
    city: String,
    onExitClick: () -> Unit,
    onEditClick: () -> Unit,
    onTabClick: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 60.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Выход",
                color = Color.White,
                fontFamily = Playfair,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(onClick = onExitClick) {
                Icon(
                    painter = painterResource(id = R.drawable.exit),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(380.dp))


            Column(
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileChip(text = name.ifBlank { "Имя не задано" })
                ProfileChip(text = if (date.isBlank()) "Дата рождения не задана" else "Дата рождения: $date")
                ProfileChip(text = if (time.isBlank()) "Время рождения не задана" else "Время рождения: $time")
                ProfileChip(text = if (city.isBlank()) "Город не выбран" else "Город: $city")

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onEditClick
                ) {
                    Text("Изменить")
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            BottomBar(selected = selected, onTabClick = onTabClick)
        }
    }
}
@Composable
fun BottomBar(
    selected: Int,
    onTabClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFF14091F)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomTab(
            title = "фаза луны",
            selected = selected == 0,
            iconNone = R.drawable.moon_none,
            iconColor = R.drawable.moon_color,
            onClick = { onTabClick(0) }
        )
        BottomTab(
            title = "советы",
            selected = selected == 1,
            iconNone = R.drawable.advice_none,
            iconColor = R.drawable.advice_color,
            onClick = { onTabClick(1) }
        )
        BottomTab(
            title = "профиль",
            selected = selected == 2,
            iconNone = R.drawable.profile_none,
            iconColor = R.drawable.profile_color,
            onClick = { onTabClick(2) }
        )
    }
}

@Composable
fun BottomTab(
    title: String,
    selected: Boolean,
    iconNone: Int,
    iconColor: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(id = if (selected) iconColor else iconNone),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = title, color = Color.White, fontSize = 13.sp)
    }
}

@Composable
fun AdviceCard(title: String, text: String, icon: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5B3B8C)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Column {
                Text(title, color = Color.White, fontSize = 24.sp, fontFamily = Playfair)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text, color = Color.White, fontSize = 16.sp)
            }
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(90.dp)
            )
        }
    }
}

@Composable
fun AdviceMiniCard(title: String, icon: Int, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0C6E0)),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .height(350.dp)
            .clickable { /* заглушка */ }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                title,
                color = Color(0xFF4A255F),
                fontSize = 20.sp,
                fontFamily = Playfair
            )
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(90.dp)
            )
        }
    }
}

@Composable
fun ProfileChip(text: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0C6E0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            color = Color(0xFF3B1C4B),
            fontSize = 18.sp
        )
    }
}