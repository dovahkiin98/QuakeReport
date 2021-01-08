package net.inferno.quakereport.view

import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
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
    if(animationStyle == RefreshAnimationStyle.MOVE) {
        val transition = transition(
            definition = moveTransitionDefinition,
            toState = if (enabled) Visibility.VISIBLE else Visibility.GONE,
        )
        if (transition[bottomOffset] > 0.dp) {
            Surface(
                shape = CircleShape,
                elevation = 4.dp,
                modifier = modifier
                    .offset(y = transition[bottomOffset])
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
        val transition = transition(
            definition = scaleTransitionDefinition,
            toState = if (enabled) Visibility.VISIBLE else Visibility.GONE,
        )
        if (transition[scale] > 0) {
            Surface(
                shape = CircleShape,
                elevation = 4.dp,
                modifier = modifier
                    .offset(y = 24.dp)
                    .preferredSize(40.dp * transition[scale]),
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

private val bottomOffset = DpPropKey("Bottom Offset")
private val scale = FloatPropKey("Scale")

enum class RefreshAnimationStyle {
    SCALE,
    MOVE,
}

private val moveTransitionDefinition = transitionDefinition<Visibility> {
    state(Visibility.GONE) {
        this[bottomOffset] = (-24).dp
    }
    state(Visibility.VISIBLE) {
        this[bottomOffset] = 24.dp
    }

    transition(Visibility.VISIBLE to Visibility.GONE) {
        bottomOffset using tween(durationMillis = 500)
    }

    transition(Visibility.GONE to Visibility.VISIBLE) {
        bottomOffset using tween(durationMillis = 500)
    }
}

private val scaleTransitionDefinition = transitionDefinition<Visibility> {
    state(Visibility.GONE) {
        this[scale] = 0f
    }
    state(Visibility.VISIBLE) {
        this[scale] = 1f
    }

    transition(Visibility.VISIBLE to Visibility.GONE) {
        scale using tween(durationMillis = 500)
    }

    transition(Visibility.GONE to Visibility.VISIBLE) {
        scale using tween(durationMillis = 500)
    }
}

private enum class Visibility {
    VISIBLE,
    GONE,
}

@Preview
@Composable
fun RefreshDrawablePreview() {
    RefreshDrawable(enabled = true)
}