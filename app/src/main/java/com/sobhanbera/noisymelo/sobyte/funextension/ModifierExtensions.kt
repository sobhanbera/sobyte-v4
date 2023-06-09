package com.sobhanbera.noisymelo.sobyte.funextension

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.*
import kotlin.math.*

/**
 * This function is used to create a gradient background
 * by extending the Modifier class
 * and adding a rectangle with a linear gradient brush
 *
 * @param colors the list of colors to be used in the gradient
 * @param angle the angle of the gradient
 * @return the modifier with the gradient background
 */
fun Modifier.gradientBackground(
	colors: List<Color>,
	angle: Float
) = this.then(
	Modifier.drawBehind {
		// Get the center of the canvas
		val angleRad = angle / 180f * PI
		val x = cos(angleRad).toFloat() //Fractional x
		val y = sin(angleRad).toFloat() //Fractional y

		// Get the radius of the gradient
		val radius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2f
		// Get the center of the canvas
		val offset = center + Offset(x * radius, y * radius)

		// Get the exact offset
		val exactOffset = Offset(
			x = min(offset.x.coerceAtLeast(0f), size.width),
			y = size.height - min(offset.y.coerceAtLeast(0f), size.height)
		)

		// Draw the rectangle with the gradient
		drawRect(
			brush = Brush.linearGradient(
				colors = colors,
				start = Offset(size.width, size.height) - exactOffset,
				end = exactOffset
			),
			size = size
		)
	}
)

/**
 * this enum hold the state of the button
 * particularly if it is clicked or not
 */
enum class ButtonClickState {
	CLICKED,
	IDLE
}

/**
 * This function makes any composable as scalable when clicked
 * if scaleOnClick is used the composable will scale to the
 * given value when clicked
 *
 * @param scaleOnClick the scale value to be used when clicked
 * @return the modifier with the scaleOnClick functionality
 */
fun Modifier.scaleOnClick(
	scaleOnClick: Float = 0.95f,
) = composed {
	// state to manage the button clicked state
	// 2nd state to manage the scale value/animated scale value
	var buttonState by remember {
		mutableStateOf(ButtonClickState.IDLE)
	}

	val scale by animateFloatAsState(
		if (buttonState == ButtonClickState.CLICKED)
			scaleOnClick
		else 1f
	)

	this
		.graphicsLayer {
			scaleX = scale // main scale property applied here
			scaleY = scale // main scale property applied here
		}
		.clickable(
			interactionSource = remember { MutableInteractionSource() },
			indication = null,
			onClick = { }
		)
		.pointerInput(buttonState) {
			awaitPointerEventScope {
				// changes the animation based on what is the current state of the button
				buttonState = if (buttonState == ButtonClickState.CLICKED) {
					waitForUpOrCancellation()
					ButtonClickState.IDLE
				} else {
					awaitFirstDown(false)
					ButtonClickState.CLICKED
				}
			}
		}
}