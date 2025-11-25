package com.example.assign6_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assign6_2.ui.theme.Assign6_2Theme
import com.example.assign6_2.SensorHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assign6_2Theme {
                CompassLevelScreen(SensorHelper(this))
            }
        }
    }
}

@Composable
fun CompassLevelScreen(helper: SensorHelper) {
    val heading by helper.heading.collectAsState()
    val pitch by helper.pitch.collectAsState()
    val roll by helper.roll.collectAsState()

    DisposableEffect(Unit) {
        helper.start()
        onDispose { helper.stop() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color.Blue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Text(
            "Compass",
            fontSize = 28.sp
        )

        Compass(heading = heading)

        Text(
            "Level",
            fontSize = 28.sp
        )

        Level(pitch = pitch, roll = roll)
    }
}

@Composable
fun Compass(heading: Float) {

    val animatedHeading by animateFloatAsState(
        targetValue = heading,
        animationSpec = tween(500, easing = LinearOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .size(260.dp)
            .background(Color(0xFF1F2A38), shape = CircleShape)
            .border(4.dp, Color.LightGray, CircleShape)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            rotate(-animatedHeading) {
                drawLine(
                    start = center,
                    end = Offset(center.x, 20f),
                    strokeWidth = 10f,
                    color = Color.Red,
                    cap = StrokeCap.Round
                )
            }
        }

        Text(
            text = "${heading.toInt()}°",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Level(pitch: Float, roll: Float) {

    val size = 200.dp
    val bubbleSize = 40.dp

    val density = LocalDensity.current
    val maxOffsetPx = with(density) { ((size - bubbleSize) / 2).toPx() }

    val bubbleX = ((pitch / 90f) * maxOffsetPx).coerceIn(-maxOffsetPx, maxOffsetPx) + 200
    val bubbleY = ((roll / 180f) * maxOffsetPx).coerceIn(-maxOffsetPx, maxOffsetPx) + 100

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(size)
                .background(Color(0xFF1F2A38), shape = CircleShape)
                .border(3.dp, Color.White, CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(bubbleX.toInt(), bubbleY.toInt()) }
                    .size(bubbleSize)
                    .background(Color(0xFF00E6B8), shape = CircleShape)
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Pitch: ${"%.1f".format(pitch)}°   Roll: ${"%.1f".format(roll)}°",
            fontSize = 18.sp
        )
    }
}



