package net.inferno.quakereport.view

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RefreshDrawable(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    animationStyle: RefreshAnimationStyle = RefreshAnimationStyle.SCALE,
) {
    // Show Jump to Bottom button
    if (animationStyle == RefreshAnimationStyle.MOVE) {
        val transition by animateDpAsState(
            if (enabled) 24.dp else (-24).dp
        )
        if (transition > 0.dp) {
            Surface(
                shape = CircleShape,
                elevation = 4.dp,
                modifier = modifier
                    .offset(y = transition)
                    .preferredSize(40.dp),
            ) {
                CircularProgressIndicator(
                    strokeWidth = 3.dp,
                    modifier = Modifier
                        .padding(10.dp),
                )
            }
        }
    } else {
        val transition by animateFloatAsState(
            if (enabled) 1f else 0f
        )
        if (transition > 0f) {
            Surface(
                shape = CircleShape,
                elevation = 4.dp,
                modifier = modifier
                    .offset(y = 24.dp)
                    .preferredSize(40.dp * transition),
            ) {
                CircularProgressIndicator(
                    strokeWidth = 3.dp,
                    modifier = Modifier
                        .padding(10.dp),
                )
            }
        }
    }
}

enum class RefreshAnimationStyle {
    SCALE,
    MOVE,
}

@Preview
@Composable
fun RefreshDrawablePreview() {
    RefreshDrawable(enabled = true)
}