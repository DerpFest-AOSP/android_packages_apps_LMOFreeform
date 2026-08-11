package com.libremobileos.freeform.server.ui

import android.annotation.SuppressLint
import android.util.Slog
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.libremobileos.freeform.server.LMOFreeformServiceHolder
import com.libremobileos.freeform.server.SystemServiceHolder
import kotlin.math.abs
import kotlin.math.roundToInt

class MoveTouchListener(
    private val window: FreeformWindow
) : View.OnTouchListener{
    private var startX = 0.0f
    private var startY = 0.0f
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                window.bringToFront()
                startX = event.rawX
                startY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                window.windowParams.apply {
                    x = (x + event.rawX - startX).roundToInt()
                    y = (y + event.rawY - startY).roundToInt()
                }
                window.updateWindowLayout()
                startX = event.rawX
                startY = event.rawY
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                window.makeSureFreeformInScreen()
            }
        }
        return true
    }
}

class LeftViewClickListener(private val window: FreeformWindow) : View.OnClickListener {
    override fun onClick(v: View) {
        window.bringToFront()
        window.close()
    }

}

/**
 * maximize freeform screen
 */
class MaximizeClickListener(private val window: FreeformWindow): View.OnClickListener {
    companion object {
        private const val TAG = "LMOFreeform/TouchListener"
    }
    override fun onClick(v: View) {
        window.bringToFront()
        if (null != window.freeformTaskStackListener) {
            val taskId = window.freeformTaskStackListener!!.taskId
            if (taskId == -1) {
                Slog.e(TAG, "taskId is -1, cannot move")
                return
            }
            window.postOnWorkerHandler {
                runCatching { SystemServiceHolder.activityTaskManager.moveRootTaskToDisplay(taskId, Display.DEFAULT_DISPLAY) }
                    .onFailure { Slog.e(TAG, "moveRootTaskToDisplay failed: $it") }
            }
        }
    }
}

/**
 * Pin freeform
 */
class PinClickListener(private val window: FreeformWindow): View.OnClickListener {
    override fun onClick(v: View) {
        window.bringToFront()
        window.postOnHandler {
            // hangup
            handleHangUp()
        }
    }
}

class RightViewClickListener(private val displayId: Int) : View.OnClickListener {
    override fun onClick(v: View) {
        LMOFreeformServiceHolder.back(displayId)
    }
}

class ScaleTouchListener(private val window: FreeformWindow, private val isRight: Boolean = true): View.OnTouchListener {
    private var startX = 0.0f
    private var startWidth = 0
    private var startHeight = 0
    private var startLayoutWidth = 0
    private var startLayoutHeight = 0
    private var startWindowX = 0
    private var startWindowY = 0
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                window.bringToFront()
                startX = event.rawX
                startWidth = window.freeformRootView.layoutParams.width
                startHeight = window.freeformRootView.layoutParams.height
                startLayoutWidth = window.freeformLayout.width.takeIf { it > 0 } ?: startWidth
                startLayoutHeight = window.freeformLayout.height.takeIf { it > 0 } ?: startHeight
                startWindowX = window.windowParams.x
                startWindowY = window.windowParams.y
                window.freeformRootView.visibility = View.INVISIBLE
                window.veilView.visibility = View.VISIBLE
            }
            MotionEvent.ACTION_MOVE -> {
                val xDelta = if (isRight) (event.rawX - startX) else (startX - event.rawX)
                window.resizeFreeformTo(
                    startWidth + xDelta,
                    isRight,
                    startWidth,
                    startHeight,
                    startLayoutWidth,
                    startLayoutHeight,
                    startWindowX,
                    startWindowY
                )
            }
            MotionEvent.ACTION_UP -> {
                if (window.freeformView.surfaceTexture != null) {
                    window.freeformConfig.width = window.freeformRootView.layoutParams.width
                    window.freeformConfig.height = window.freeformRootView.layoutParams.height
                    window.postOnHandler { makeSureFreeformInScreen() }
                    // Resize only the overlay. Keeping the virtual display and its buffer at
                    // their initial size prevents apps from receiving a display config change.
                    window.measureScale(updateDisplaySize = false)
                    window.postOnHandlerDelayed(250) {
                        freeformRootView.visibility = View.VISIBLE
                        veilView.visibility = View.GONE
                    }
                } else {
                    window.freeformRootView.visibility = View.VISIBLE
                    window.veilView.visibility = View.GONE
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                window.freeformRootView.visibility = View.VISIBLE
                window.veilView.visibility = View.GONE
            }
        }
        return true
    }
}

class MinimizedIconTouchListener(private val window: FreeformWindow) : View.OnTouchListener {
    private var startRawY = 0f
    private var startWindowY = 0
    private var hasMoved = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                window.bringToFront()
                startRawY = event.rawY
                startWindowY = window.windowParams.y
                hasMoved = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = event.rawY - startRawY
                if (abs(dy) > ViewConfiguration.get(v.context).scaledTouchSlop) {
                    hasMoved = true
                }
                val iconSizePx = window.windowParams.height
                val displayHeight = window.getFreeformDisplayHeight()
                val maxY = displayHeight / 2 - iconSizePx / 2
                val minY = -(displayHeight / 2 - iconSizePx / 2)
                window.windowParams.apply {
                    y = (startWindowY + dy).roundToInt().coerceIn(minY, maxY)
                }
                window.updateWindowLayout()
            }
            MotionEvent.ACTION_UP -> {
                if (!hasMoved) {
                    window.postOnHandler { handleHangUp() }
                }
            }
        }
        return true
    }
}
