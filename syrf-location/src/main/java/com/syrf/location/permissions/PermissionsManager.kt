package com.syrf.location.permissions

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.syrf.location.interfaces.SYRFTimber
import com.syrf.location.configs.SYRFPermissionRequestConfig

/**
 * The helping class make easier to work with runtime permission
 * @property context The context that handle permission. Should be activity
 */
class PermissionsManager(val context: Activity) {

    /**
     * Determine whether a particular permission has been granted.
     * @param permission The permission name
     */
    fun isPermissionGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    /**
     * Show a "in app" dialog that provide the reason why we need permissions
     * before actually requesting those permissions using system dialogs.
     * @param config Contains params for title, message, positive and negative buttons
     * @param onPositionClick The callback will be called when user click positive button
     * @param onNegativeClick The callback will be called when user click negative button
     */
    fun showPermissionReasonAndRequest(
        config: SYRFPermissionRequestConfig,
        onPositionClick: () -> Unit,
        onNegativeClick: () -> Unit,
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(config.title)
        alertDialogBuilder.setMessage(config.message)
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton(config.okButton) { dialog, _ ->
            onPositionClick.invoke()
            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton(config.cancelButton) { dialog, _ ->
            onNegativeClick.invoke()
            dialog.dismiss()
        }
        alertDialogBuilder.show()
    }

    /**
     * Handle the result from requesting permissions
     * @param permissions The requested permissions.
     * @param successCallback The callback will be called when all permissions are granted
     * @param exceptionCallback The callback will be called when one of permissions is not granted
     */
    fun handleResults(
        permissions: Array<out String>,
        successCallback: () -> Unit,
        exceptionCallback: () -> Unit
    ) {

        if (permissions.isEmpty()) {
            exceptionCallback()
            SYRFTimber.e("Permissions denied")
            return
        }

        for (permission in permissions) {
            if (!isPermissionGranted(permission)) {
                exceptionCallback()
                if (isUserCheckNeverAskAgain(permission)) {
                    SYRFTimber.i("User checked never ask again for $permission")
                }
                SYRFTimber.e("Permission: $permission denied")
                return
            }
        }

        successCallback()
        SYRFTimber.d("Permissions granted")
    }

    /**
     *  Determine whether user checked never ask again button in system's permission request dialog.
     */
    private fun isUserCheckNeverAskAgain(permission: String) =
        !ActivityCompat.shouldShowRequestPermissionRationale(
            context,
            permission
        )
}