package com.libremobileos.freeform.server.ui

import android.app.ActivityManager
import android.app.ITaskStackListener
import android.content.ComponentName
import android.view.Display
import com.libremobileos.freeform.server.util.Debug.dlog

class FreeformTaskStackListener(
    private val displayId: Int,
    private val window: FreeformWindow
) : ITaskStackListener.Stub() {

    var taskId = -1

    companion object {
        private const val TAG = "LMOFreeform/FreeformTaskStackListener"
    }

    override fun onTaskStackChanged() {

    }

    override fun onActivityPinned(packageName: String, userId: Int, taskId: Int, stackId: Int) {

    }

    override fun onActivityUnpinned() {

    }

    override fun onActivityRestartAttempt(
        task: ActivityManager.RunningTaskInfo?,
        homeTaskVisible: Boolean,
        clearedTask: Boolean,
        wasVisible: Boolean
    ) {

    }

    override fun onActivityForcedResizable(packageName: String, taskId: Int, reason: Int) {

    }

    override fun onActivityDismissingDockedTask() {

    }

    override fun onActivityLaunchOnSecondaryDisplayFailed(
        taskInfo: ActivityManager.RunningTaskInfo?,
        requestedDisplayId: Int
    ) {

    }

    override fun onActivityLaunchOnSecondaryDisplayRerouted(
        taskInfo: ActivityManager.RunningTaskInfo?,
        requestedDisplayId: Int
    ) {

    }

    override fun onTaskCreated(taskId: Int, componentName: ComponentName?) {

    }

    override fun onTaskRemoved(taskId: Int) {
        if (this.taskId == taskId) {
            dlog(TAG, "onTaskRemoved $taskId")
            window.postOnHandler { destroy("onTaskRemoved", true) }
        }
    }

    override fun onTaskMovedToFront(taskInfo: ActivityManager.RunningTaskInfo?) {
        val displayId = taskInfo?.displayId ?: return
        if (this.displayId == displayId) {
            // TODO: move to android.provider.Settings
            // if (FreeformWindowManager.settings.showImeInFreeform) {
            //     SystemServiceHolder.windowManager.setDisplayImePolicy(displayId, 0)
            // }
            taskId = taskInfo.taskId
            dlog(TAG, "onTaskMovedToFront $taskId")
        }
    }

    override fun onTaskDescriptionChanged(taskInfo: ActivityManager.RunningTaskInfo?) {
        val displayId = taskInfo?.displayId ?: return
        if (this.displayId == displayId) {
            taskId = taskInfo.taskId
            dlog(TAG, "onTaskDescriptionChanged $taskInfo")
        }
    }

    override fun onActivityRequestedOrientationChanged(taskId: Int, requestedOrientation: Int) {
        if (taskId == this.taskId) {
            dlog(TAG, "onActivityRequestedOrientationChanged: $requestedOrientation")
            window.onActivityRequestedOrientationChanged(requestedOrientation)
        }
    }

    override fun onTaskRemovalStarted(taskInfo: ActivityManager.RunningTaskInfo?) {
        val displayId = taskInfo?.displayId ?: return
        if (this.displayId == displayId) {
            taskId = taskInfo.taskId
            dlog(TAG, "onTaskRemovalStarted $taskId")
            // window.removeView()
        }
    }

    override fun onTaskProfileLocked(taskInfo: ActivityManager.RunningTaskInfo, userId: Int) {

    }

    override fun onBackPressedOnTaskRoot(taskInfo: ActivityManager.RunningTaskInfo?) {

    }

    override fun onTaskDisplayChanged(taskId: Int, newDisplayId: Int) {
        if (taskId == this.taskId && newDisplayId == Display.DEFAULT_DISPLAY) {
            window.postOnHandler { destroy("onTaskDisplayChanged: $taskId to main display") }
        } else if (newDisplayId == displayId) {
            this.taskId = taskId
            dlog(TAG, "onTaskDisplayChanged: $taskId to freeform display")
        }
    }

    override fun onRecentTaskListUpdated() {

    }

    override fun onRecentTaskListFrozenChanged(frozen: Boolean) {

    }

    override fun onRecentTaskRemovedForAddTask(taskId: Int) {

    }

    override fun onTaskFocusChanged(taskId: Int, focused: Boolean) {

    }

    override fun onActivityRotation(displayId: Int) {

    }

    override fun onTaskMovedToBack(taskInfo: ActivityManager.RunningTaskInfo?) {

    }

    override fun onLockTaskModeChanged(mode: Int) {

    }

}
