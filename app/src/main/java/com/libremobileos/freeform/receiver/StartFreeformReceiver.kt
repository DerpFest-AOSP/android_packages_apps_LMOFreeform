package com.libremobileos.freeform.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.libremobileos.freeform.LMOFreeform
import com.libremobileos.freeform.LMOFreeformServiceManager
import com.libremobileos.freeform.R
import com.libremobileos.freeform.utils.Debug
import com.libremobileos.freeform.utils.Logger
import kotlin.math.roundToInt

/**
 * @author KindBrave
 * @since 2023/9/19
 */
class StartFreeformReceiver : BroadcastReceiver() {
    private val logger = Logger(TAG)
    companion object {
        private const val TAG = "StartFreeformReceiver"
        private const val ACTION = "com.libremobileos.freeform.START_FREEFORM"
        private const val INITIAL_MAX_WIDTH = 600
        private const val INITIAL_MAX_HEIGHT = 600
    }
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION) {
            if (Debug.isDebug) logger.d("onReceive ${intent.extras}")
            val packageName = intent.getStringExtra("packageName")
            val activityName = intent.getStringExtra("activityName")
            val userId = intent.getIntExtra("userId", 0)
            val taskId = intent.getIntExtra("taskId", -1)

            if (packageName != null && activityName != null) {
                if (!LMOFreeformServiceManager.ping()) {
                    Toast.makeText(
                        context,
                        R.string.freeform_service_unavailable,
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                
                val sp = context.getSharedPreferences(LMOFreeform.CONFIG, Context.MODE_PRIVATE)
                val screenWidth = context.resources.displayMetrics.widthPixels
                val screenHeight = context.resources.displayMetrics.heightPixels
                val screenDensityDpi = context.resources.displayMetrics.densityDpi
                
                val freeformWidth = sp.getInt("freeform_width", (screenWidth * 0.6f).roundToInt())
                    .coerceAtMost(INITIAL_MAX_WIDTH)
                val freeformHeight = sp.getInt("freeform_height", (screenHeight * 0.5f).roundToInt())
                    .coerceAtMost(INITIAL_MAX_HEIGHT)
                
                val success = LMOFreeformServiceManager.createWindow(
                    packageName,
                    activityName,
                    userId,
                    taskId,
                    freeformWidth,
                    freeformHeight,
                    sp.getInt("freeform_dpi", screenDensityDpi),
                )
                
                if (!success) {
                    Toast.makeText(
                        context,
                        R.string.freeform_window_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
