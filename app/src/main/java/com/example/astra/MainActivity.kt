package com.example.astra

import com.example.astra.MatrixViewModel
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
import java.time.LocalDateTime
import com.example.astra.R
import java.util.Calendar
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.draw.clip
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.astra.model.City



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

enum class AdviceScreen {
    MAIN,
    HOROSCOPE,
    MATRIX
}

@Composable
fun AstraApp() {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var city by remember { mutableStateOf<City?>(null) }
    var started by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedMoonDate by remember {
        mutableStateOf(LocalDate.now())
    }

    var adviceScreen by remember {
        mutableStateOf(AdviceScreen.MAIN)
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
                    selectedDate = selectedMoonDate,
                    onDateChange = { selectedMoonDate = it },
                    onTabClick = { pageIndex ->
                        scope.launch { pagerState.animateScrollToPage(pageIndex) }
                    }
                )
                1 -> {
                    when (adviceScreen) {
                        AdviceScreen.MAIN -> AdvicePage(
                            selected = pagerState.currentPage,
                            userBirthDate = date,
                            onTabClick = { pageIndex ->
                                scope.launch { pagerState.animateScrollToPage(pageIndex) }
                            },
                            onHoroscopeClick = { adviceScreen = AdviceScreen.HOROSCOPE },
                            onMatrixClick = { adviceScreen = AdviceScreen.MATRIX }
                        )
                        AdviceScreen.HOROSCOPE -> HoroscopePage(
                            userBirthDate = date,
                            onBackClick = { adviceScreen = AdviceScreen.MAIN }
                        )
                        AdviceScreen.MATRIX -> {
                            val selectedCity = city
                            if (selectedCity != null) {
                                MatrixPage(
                                    birthDate = date,
                                    birthTime = time,
                                    city = selectedCity,
                                    onBackClick = { adviceScreen = AdviceScreen.MAIN }
                                )
                            } else {
                                adviceScreen = AdviceScreen.MAIN
                            }
                        }
                    }
                }
                2 -> ProfilePage(
                    selected = pagerState.currentPage,
                    name = name,
                    date = date,
                    time = time,
                    city = city,
                    onExitClick = { (context as? ComponentActivity)?.finish() },
                    onEditClick = { started = false },
                    onTabClick = { pageIndex ->
                        scope.launch { pagerState.animateScrollToPage(pageIndex) }
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
    onCityChange: (City) -> Unit
) {
    val viewModel: MatrixViewModel = viewModel()

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
    var cityInput by rememberSaveable { mutableStateOf("") }      // текст в поле
    var selectedCity by rememberSaveable { mutableStateOf<City?>(null) } // выбранный город
    var date by rememberSaveable { mutableStateOf("") }
    var time by rememberSaveable { mutableStateOf("") }
    var showError by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.bgbg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 10.dp),
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
                    Text(
                        text = "Введите имя",
                        modifier = Modifier.padding(top = 3.dp)
                    )
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
                                date = "%02d.%02d.%04d".format(day, month + 1, year)
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
                            text = "Дата рождения",
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 3.dp)
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
                                time = "%02d:%02d".format(hour, minute)
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
                            text = "Время рождения",
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    },
                    colors = whiteFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            CityAutocomplete(
                value = cityInput,
                onValueChange = {
                    cityInput = it
                    selectedCity = null
                    viewModel.searchCity(it)
                },
                suggestions = viewModel.citySuggestions,
                onSelect = { picked ->
                    selectedCity = picked
                    cityInput = picked.name
                    onCityChange(picked)
                    viewModel.clearCitySuggestions()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (showError) {
                Text(
                    text = "Пожалуйста, заполните все поля",
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            IconButton(
                onClick = {
                    if (name.isBlank() || date.isBlank() || time.isBlank() || selectedCity == null) {
                        showError = true
                    } else {
                        showError = false
                        onNextClick()
                    }
                },
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_circle_right_24),
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
fun MoonCalendarCard(
    month: YearMonth,
    selectedDate: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {

    val firstDay = month.atDay(1)
    val startOffset = (firstDay.dayOfWeek.value + 6) % 7
    val daysInMonth = month.lengthOfMonth()

    val cells = mutableListOf<LocalDate?>()

    repeat(startOffset) {
        cells.add(null)
    }

    repeat(daysInMonth) {
        cells.add(month.atDay(it + 1))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = 40.dp,
            topEnd = 40.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A2358).copy(alpha = 0.92f)
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "<",
                    color = Color.White,
                    fontSize = 28.sp,
                    modifier = Modifier.clickable {
                        onPreviousMonth()
                    }
                )

                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    text = month.month.getDisplayName(
                        TextStyle.FULL,
                        Locale("ru")
                    ).replaceFirstChar { it.uppercase() } +
                            " ${month.year}",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontFamily = Playfair
                )

                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    ">",
                    color = Color.White,
                    fontSize = 28.sp,
                    modifier = Modifier.clickable {
                        onNextMonth()
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val weekDays = listOf(
                "Пн", "Вт", "Ср", "Чт",
                "Пт", "Сб", "Вс"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weekDays.forEach {
                    Text(
                        text = it,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(190.dp),
                userScrollEnabled = false
            ) {

                items(cells) { date ->

                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(30.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        if (date != null) {

                            val isSelected =
                                date == selectedDate

                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(
                                        RoundedCornerShape(8.dp)
                                    )
                                    .background(
                                        if (isSelected)
                                            Color(0xFFB12BE4)
                                        else
                                            Color.Transparent
                                    )
                                    .clickable {
                                        onDateSelected(date)
                                    },
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = date.dayOfMonth.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun MoonPage(
    selected: Int,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    onTabClick: (Int) -> Unit
) {

    var currentMonth by remember {
        mutableStateOf(YearMonth.now())
    }

    var selectedDate by remember {
        mutableStateOf(LocalDate.now())
    }

    val moonViewModel: MoonViewModel = viewModel()

    LaunchedEffect(Unit) {
        moonViewModel.loadMoon(
            LocalDateTime.now().toString()
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(R.drawable.moon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(
                    getMoonImage(moonViewModel.moonPhase)
                ),
                contentDescription = null,
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = moonViewModel.moonPhase,
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = Playfair
            )

            Text(
                text = "${moonViewModel.illumination}%",
                color = Color.White,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))



            Spacer(modifier = Modifier.weight(1f))

            MoonCalendarCard(
                month = currentMonth,
                selectedDate = selectedDate,
                onPreviousMonth = {
                    currentMonth = currentMonth.minusMonths(1)
                },
                onNextMonth = {
                    currentMonth = currentMonth.plusMonths(1)
                },
                onDateSelected = { date ->

                    selectedDate = date

                    moonViewModel.loadMoon(
                        date.toString() + "T00:00:00"
                    )
                }
            )

            BottomBar(
                selected = selected,
                onTabClick = onTabClick
            )
        }
    }
}

@Composable
fun AdvicePage(
    selected: Int,
    userBirthDate: String,
    onTabClick: (Int) -> Unit,
    onHoroscopeClick: () -> Unit,
    onMatrixClick: () -> Unit
) {
    val moonViewModel: MoonViewModel = viewModel()

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.advice),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            AdviceCard(
                title = "Совет от луны",
                text = moonViewModel.moonText,
                icon = R.drawable.cat
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                val sign = getZodiacSign(userBirthDate)

                AdviceMiniCard(
                    title = "Гороскоп дня",
                    icon = zodiacIcon(sign),
                    cardColor = Color(0xFFE8C8FF),
                    textColor = Color(0xFF6A1B9A),
                    modifier = Modifier.weight(1f),
                    onClick = onHoroscopeClick
                )

                Spacer(modifier = Modifier.width(12.dp))

                AdviceMiniCard(
                    title = "Матрица дня",
                    icon = R.drawable.woman,
                    cardColor = Color(0xFFC6F2E6),
                    textColor = Color(0xFF00695C),
                    modifier = Modifier.weight(1f),
                    onClick = onMatrixClick
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            BottomBar(
                selected = selected,
                onTabClick = onTabClick
            )
        }
    }
}
@Composable
fun ProfilePage(
    selected: Int,
    name: String,
    date: String,
    time: String,
    city: City?,                     // ← было String
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(380.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileChip(text = name.ifBlank { "Имя не задано" })

                ProfileChip(
                    text = if (date.isBlank()) "Дата рождения не задана" else "Дата рождения: $date"
                )

                ProfileChip(
                    text = if (time.isBlank()) "Время рождения не задано" else "Время рождения: $time"
                )

                ProfileChip(
                    text = if (city == null) "Город не выбран" else "Город: ${city.name} ${countryFlag(city.country)}"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B3B8C)),
                    shape = RoundedCornerShape(20.dp)
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
fun HoroscopePage(
    userBirthDate: String,
    onBackClick: () -> Unit
){

    val viewModel: HoroscopeViewModel = viewModel()
    val sign = getZodiacSign(userBirthDate)

    LaunchedEffect(Unit) {
        viewModel.load(sign)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(R.drawable.matrix),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 70.dp,
                bottom = 40.dp
            )
        ) {

            item {

                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.baseline_arrow_circle_right_24
                        ),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(42.dp)
                            .graphicsLayer {
                                scaleX = -1f
                            }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(
                            zodiacIcon(viewModel.sign)
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(180.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF5B3B8C)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {

                    Text(
                        text = viewModel.text,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MatrixPage(
    birthDate: String,
    birthTime: String,
    city: City,
    onBackClick: () -> Unit
) {
    val viewModel: MatrixViewModel = viewModel()

    LaunchedEffect(city) {
        viewModel.load(
            date = birthDate,
            time = birthTime,
            city = city
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.matrix),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 70.dp, bottom = 40.dp)
        ) {
            item {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_circle_right_24),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(42.dp).graphicsLayer { scaleX = -1f }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (viewModel.loading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    VedicChartCanvas(
                        ascendant = viewModel.ascendant,
                        planets = viewModel.planets,
                        modifier = Modifier.fillMaxWidth().height(400.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF5B3B8C)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = viewModel.description,
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            }
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
fun AdviceCard(
    title: String,
    text: String,
    icon: Int
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF5B3B8C)
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // было height(300.dp) — оставляем
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                item {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontFamily = Playfair
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = text,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(90.dp)
            )
        }
    }
}

@Composable
fun AdviceMiniCard(
    title: String,
    icon: Int,
    cardColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .height(340.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Text(
                text = title,
                color = textColor,
                fontSize = 20.sp,
                fontFamily = Playfair
            )
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(100.dp)
            )
        }
    }
}

@Composable
fun ProfileChip(text: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0C6E0)
        ),
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


@Composable
fun VedicChartCanvas(
    ascendant: com.example.astra.model.Ascendant?,
    planets: List<com.example.astra.model.Planet>,
    modifier: Modifier = Modifier
) {
    val zodiacRu = listOf(
        "Овен", "Телец", "Близнецы", "Рак",
        "Лев", "Дева", "Весы", "Скорпион",
        "Стрелец", "Козерог", "Водолей", "Рыбы"
    )
    val zodiacSymbols = listOf(
        "♈", "♉", "♊", "♋", "♌", "♍",
        "♎", "♏", "♐", "♑", "♒", "♓"
    )
    val zodiacEn = listOf(
        "Aries", "Taurus", "Gemini", "Cancer",
        "Leo", "Virgo", "Libra", "Scorpio",
        "Sagittarius", "Capricorn", "Aquarius", "Pisces"
    )
    val planetSymbols = mapOf(
        "Su" to "☀", "Mo" to "☽", "Ma" to "♂",
        "Me" to "☿", "Ju" to "♃", "Ve" to "♀",
        "Sa" to "♄", "Ra" to "☊", "Ke" to "☋",
        "Ur" to "♅", "Ne" to "♆", "Pl" to "♇"
    )

    val ascSignIndex = ascendant?.let {
        zodiacEn.indexOfFirst { s -> s.equals(it.sign, ignoreCase = true) }
    }?.takeIf { it >= 0 } ?: 0

    val planetsByHouse = mutableMapOf<Int, MutableList<String>>()
    for (p in planets) {
        val abbr = p.name.take(2)
        planetsByHouse.getOrPut(p.house) { mutableListOf() }.add(abbr)
    }

    val cellPositions = listOf(
        Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(0, 3),
        Pair(1, 3), Pair(2, 3), Pair(3, 3), Pair(3, 2),
        Pair(3, 1), Pair(3, 0), Pair(2, 0), Pair(1, 0)
    )


    val elementColors = listOf(
        android.graphics.Color.argb(60, 220, 80, 80),   // Огонь: 1,5,9
        android.graphics.Color.argb(60, 80, 180, 80),   // Земля: 2,6,10
        android.graphics.Color.argb(60, 80, 160, 220),  // Воздух: 3,7,11
        android.graphics.Color.argb(60, 80, 100, 220),  // Вода: 4,8,12
    )
    val elementColorsLight = listOf(
        android.graphics.Color.argb(120, 255, 100, 100),
        android.graphics.Color.argb(120, 100, 220, 100),
        android.graphics.Color.argb(120, 100, 200, 255),
        android.graphics.Color.argb(120, 100, 120, 255),
    )

    fun houseElement(house: Int): Int = when (house % 3) {
        1 -> 0
        2 -> 1
        0 -> if (house % 12 == 0 || house == 12) 3 else 2
        else -> 2
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cellW = w / 4f
        val cellH = h / 4f
        val cornerR = 12f


        drawRect(color = Color(0xFF0D0620))

        drawContext.canvas.nativeCanvas.apply {

            val borderPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(100, 180, 130, 255)
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 1f
                isAntiAlias = true
            }
            val fillPaint = android.graphics.Paint().apply {
                style = android.graphics.Paint.Style.FILL
                isAntiAlias = true
            }
            val glowPaint = android.graphics.Paint().apply {
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 3f
                isAntiAlias = true
                maskFilter = android.graphics.BlurMaskFilter(
                    8f, android.graphics.BlurMaskFilter.Blur.NORMAL
                )
            }

            for (i in 0..11) {
                val (row, col) = cellPositions[i]
                val left = col * cellW
                val top = row * cellH
                val right = left + cellW
                val bottom = top + cellH
                val signIndex = (ascSignIndex + i) % 12
                val houseNum = i + 1
                val elemIdx = houseElement(houseNum)


                fillPaint.color = if (i == 0)
                    android.graphics.Color.argb(100, 140, 60, 200)
                else
                    elementColors[elemIdx]
                val rect = android.graphics.RectF(left + 1, top + 1, right - 1, bottom - 1)
                drawRoundRect(rect, cornerR, cornerR, fillPaint)


                borderPaint.color = if (i == 0)
                    android.graphics.Color.argb(200, 255, 107, 157)
                else
                    android.graphics.Color.argb(80, 180, 130, 255)
                drawRoundRect(rect, cornerR, cornerR, borderPaint)


                val bgSymbolPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(25, 255, 255, 255)
                    textSize = cellH * 0.55f
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText(
                    zodiacSymbols[signIndex],
                    left + cellW / 2,
                    top + cellH * 0.72f,
                    bgSymbolPaint
                )


                val housePaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(180, 255, 255, 255)
                    textSize = cellW * 0.115f
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                drawText("$houseNum", left + cellW * 0.08f, top + cellH * 0.22f, housePaint)


                val smallSymPaint = android.graphics.Paint().apply {
                    color = elementColorsLight[elemIdx]
                    textSize = cellW * 0.13f
                    isAntiAlias = true
                }
                drawText(
                    zodiacSymbols[signIndex],
                    left + cellW * 0.08f,
                    bottom - cellH * 0.07f,
                    smallSymPaint
                )


                if (i == 0) {
                    val ascPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.argb(255, 255, 107, 157)
                        textSize = cellW * 0.13f
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                    drawText("Asc", right - cellW * 0.06f, top + cellH * 0.22f, ascPaint)


                    glowPaint.color = android.graphics.Color.argb(80, 255, 107, 157)
                    drawRoundRect(rect, cornerR, cornerR, glowPaint)
                }


                val planetsHere = planetsByHouse[houseNum] ?: emptyList()
                val planetPaint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }

                val maxPerRow = 2
                planetsHere.forEachIndexed { idx, pAbbr ->
                    val sym = planetSymbols[pAbbr] ?: pAbbr
                    val row2 = idx / maxPerRow
                    val col2 = idx % maxPerRow
                    planetPaint.textSize = cellW * 0.155f
                    planetPaint.color = when (pAbbr) {
                        "Su" -> android.graphics.Color.argb(255, 255, 200, 50)
                        "Mo" -> android.graphics.Color.argb(255, 200, 220, 255)
                        "Ma" -> android.graphics.Color.argb(255, 255, 80, 80)
                        "Me" -> android.graphics.Color.argb(255, 100, 255, 150)
                        "Ju" -> android.graphics.Color.argb(255, 255, 180, 50)
                        "Ve" -> android.graphics.Color.argb(255, 255, 150, 200)
                        "Sa" -> android.graphics.Color.argb(255, 150, 150, 220)
                        "Ra" -> android.graphics.Color.argb(255, 180, 100, 255)
                        "Ke" -> android.graphics.Color.argb(255, 130, 200, 255)
                        else -> android.graphics.Color.argb(255, 220, 220, 220)
                    }
                    val px = left + cellW * (0.12f + col2 * 0.48f)
                    val py = top + cellH * (0.42f + row2 * 0.28f)
                    drawText(sym, px, py, planetPaint)
                }
            }


            val cx = cellW
            val cy = cellH
            val cw = cellW * 2f
            val ch = cellH * 2f
            val centerRect = android.graphics.RectF(cx, cy, cx + cw, cy + ch)

            fillPaint.color = android.graphics.Color.argb(80, 60, 20, 120)
            drawRoundRect(centerRect, 20f, 20f, fillPaint)


            val mandalaPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(40, 200, 150, 255)
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 1f
                isAntiAlias = true
            }
            val midX = cx + cw / 2
            val midY = cy + ch / 2
            val r1 = cw * 0.35f
            val r2 = cw * 0.22f


            drawCircle(midX, midY, r1, mandalaPaint)
            drawCircle(midX, midY, r2, mandalaPaint)


            for (angle in 0..330 step 30) {
                val rad = Math.toRadians(angle.toDouble())
                drawLine(
                    midX + (r2 * Math.cos(rad)).toFloat(),
                    midY + (r2 * Math.sin(rad)).toFloat(),
                    midX + (r1 * Math.cos(rad)).toFloat(),
                    midY + (r1 * Math.sin(rad)).toFloat(),
                    mandalaPaint
                )
            }


            val centerBorderPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(120, 180, 130, 255)
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 1.5f
                isAntiAlias = true
            }
            drawRoundRect(centerRect, 20f, 20f, centerBorderPaint)


            val centerPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(220, 230, 200, 255)
                textSize = cellW * 0.17f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            drawText("✦ Ведическая", midX, midY - cellH * 0.08f, centerPaint)
            drawText("карта ✦", midX, midY + cellH * 0.12f, centerPaint)

            ascendant?.let {
                val subPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(180, 255, 107, 157)
                    textSize = cellW * 0.115f
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText("Асц: ${it.sign}", midX, midY + cellH * 0.32f, subPaint)
            }
        }
    }
}
@Composable
fun CityAutocomplete(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<City>,
    onSelect: (City) -> Unit
) {

    Column {

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("Город (Berlin, Moscow)", color = Color.White.copy(0.6f))
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (suggestions.isNotEmpty()) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A1835)
                )
            ) {

                Column {

                    suggestions.take(8).forEach { city ->

                        CityRow(
                            city = city,
                            query = value,
                            onClick = { onSelect(city) }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun CityRow(
    city: City,
    query: String,
    onClick: () -> Unit
) {

    val start = city.name.lowercase().indexOf(query.lowercase())

    val text = buildAnnotatedString {

        if (start >= 0) {

            append(city.name.substring(0, start))

            withStyle(
                SpanStyle(
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(city.name.substring(start, start + query.length))
            }

            append(city.name.substring(start + query.length))

        } else {
            append(city.name)
        }

        append("  ${countryFlag(city.country)}")
    }

    Text(
        text = text,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp)
    )
}