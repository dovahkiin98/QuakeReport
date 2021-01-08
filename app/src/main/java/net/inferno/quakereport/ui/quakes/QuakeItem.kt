package net.inferno.quakereport.ui.quakes

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inferno.quakereport.model.EarthQuake
import net.inferno.quakereport.theme.AppTheme
import net.inferno.quakereport.util.BrowserManager
import java.text.DecimalFormat
import java.util.*
import kotlin.math.floor
import kotlin.random.Random

@Composable
fun QuakeItem(
    earthQuake: EarthQuake,
) {
    val context = AmbientContext.current

    Row(
        modifier = Modifier
            .clickable {
                BrowserManager.openUrl(context, earthQuake.url)
            }
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(end = 16.dp)
                .background(
                    color = magnitudeColor(earthQuake.mag),
                    shape = CircleShape,
                )
                .size(48.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = DecimalFormat("0.0").format(earthQuake.mag),
                color = Color.White,
                fontSize = 16.sp,
            )
        }


        if (earthQuake.directionText != null) {
            Column {

                Row {
                    Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = earthQuake.directionText.capitalize(Locale.getDefault()),
                            modifier = Modifier
                                .weight(1f)
                        )

                        Text(
                            text = DateFormat.format("MMM dd yyyy", earthQuake.time).toString(),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }
                }

                Row {
                    Text(
                        text = earthQuake.placeText.capitalize(Locale.getDefault()),
                        modifier = Modifier
                            .weight(1f)
                    )
                    Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = DateFormat.format("hh:mm a", earthQuake.time).toString(),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        } else {
            Row {
                Text(
                    text = earthQuake.placeText.capitalize(Locale.getDefault()),
                    modifier = Modifier
                        .weight(1f)
                )

                Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                    Column(
                        horizontalAlignment = Alignment.End,
                    ) {
                        Text(
                            text = DateFormat.format("MMM dd yyyy", earthQuake.time).toString(),
                            fontSize = 14.sp,
                        )

                        Text(
                            text = DateFormat.format("hh:mm a", earthQuake.time).toString(),
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun QuakeItemPreview(
    @PreviewParameter(
        provider = EarthQuakeProvider::class,
        limit = 1,
    ) earthQuake: EarthQuake,
) {
    AppTheme {
        QuakeItem(earthQuake)
    }
}

private fun magnitudeColor(mag: Double) = when (floor(mag).toInt()) {
    0, 1 -> magnitudeColors[0]
    2 -> magnitudeColors[1]
    3 -> magnitudeColors[2]
    4 -> magnitudeColors[3]
    5 -> magnitudeColors[4]
    6 -> magnitudeColors[5]
    7 -> magnitudeColors[6]
    8 -> magnitudeColors[7]
    9 -> magnitudeColors[8]
    else -> magnitudeColors[9]
}

private val magnitudeColors = arrayOf(
    Color(0xFF4A7BA7),
    Color(0xFF04B4B3),
    Color(0xFF10CAC9),
    Color(0xFFF5A623),
    Color(0xFFFF7D50),
    Color(0xFFFC6644),
    Color(0xFFE75F40),
    Color(0xFFE13A20),
    Color(0xFFD93218),
    Color(0xFFC03823),
)

class EarthQuakeProvider : PreviewParameterProvider<EarthQuake> {
    override val values: Sequence<EarthQuake>
        get() = sequence {
            repeat(5) {
                yield(
                    EarthQuake(
                        Random.nextDouble(1.0, 10.0),
                        "10km WSW of Searles Valley, CA",
                        System.currentTimeMillis(),
                        "",
                    )
                )
            }
        }
}