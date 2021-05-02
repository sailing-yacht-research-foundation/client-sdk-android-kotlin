package permissions

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsManager(val context: Activity) {

    fun isPermissionGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun showPermissionReasonAndRequest(title: String, message: String, permissions: Array<String>, requestCode: Int) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("yes") {
                dialog, which ->  run {
            ActivityCompat.requestPermissions(context, permissions, requestCode)
            dialog.dismiss() }
        }

        alertDialogBuilder.setNegativeButton("no") {
                dialog, which ->
            dialog.dismiss()
        }
        alertDialogBuilder.show()
    }

    fun handleResults(permissions: Array<out String>, successCallback: () -> Unit, exceptionCallback: () -> Unit) {
        // It's not our expect permission
//        if (requestCode != expectRequestCode) return

        if (permissions.isEmpty()) {
            exceptionCallback()
            return
        }

        for (permission in permissions) {
            if (!isPermissionGranted(permission)) {
                exceptionCallback()
                return
            }
//        }
        }

        successCallback()


//        if (isUserCheckNeverAskAgain(context, )) {
//            // NeverAskAgain case - Never Ask Again has been checked
//            // TODO: Do something and return
//            return
//        }

        // Failure case: Not getting permission
        // Do something here
    }

    private fun isUserCheckNeverAskAgain(context: Activity, permission: String) =
            !ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    permission
            )

}