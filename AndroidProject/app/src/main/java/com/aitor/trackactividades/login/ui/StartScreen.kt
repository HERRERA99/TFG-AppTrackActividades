package com.aitor.trackactividades.login.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitor.trackactividades.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StartScreen(navigateToRegister: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Red),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Titulo
            Title(
                title = "Bienvenido",
                modifier = Modifier
                    .padding(16.dp)
                    .weight(0.2f)
                    .fillMaxWidth()
                    .background(Color.Magenta)
            )

            // Imagen
            HomeImages(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
                    .background(Color.Green)
            )

            // Botones
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth()
                    .background(Color.Yellow),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                HomeButton(
                    text = "Iniciar Sesión",
                    onClick = {  },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(8.dp)
                )
                HomeButton(
                    text = "Registrarse",
                    onClick = navigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun Title(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Magenta),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 48.sp,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeImages(modifier: Modifier = Modifier) {

    var pager = rememberPagerState(
        initialPage = 0,
        pageCount = { 5 },
        initialPageOffsetFraction = 0f
    )

    Column {
        HorizontalPager(
            state = pager,
            modifier = modifier.height(350.dp)
        ) { page ->
            Box(
                modifier =
                Modifier
                    .padding(10.dp)
                    .background(Color.Blue)
                    .fillMaxSize()
                    .aspectRatio(0.5f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = page.toString(), fontSize = 32.sp, color = Color.White)
            }
        }
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pager.pageCount) { iteration ->
                val color = if (pager.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(16.dp)
                )
            }
        }
    }
}

@Composable
fun HomeButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = text)
    }
}