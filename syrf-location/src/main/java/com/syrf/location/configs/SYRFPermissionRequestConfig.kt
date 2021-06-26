package com.syrf.location.configs

import android.content.Context
import androidx.annotation.StringRes
import com.syrf.location.R

/**
 * The class help you config params for permission's reason and request dialog
 * @property title The title of permission request dialog
 * @property message The message of permission request dialog
 * @property okButton The title of positive button in permission request dialog
 * @property cancelButton The title of negative button in  permission request dialog
 */
class SYRFPermissionRequestConfig private constructor(
    val title: String,
    val message: String,
    val okButton: String,
    val cancelButton: String,
) {

    companion object {
        /**
         * Provide a default config for using in cases client init the SDK
         * without config or missing some properties in config
         */
        fun getDefault(context: Context): SYRFPermissionRequestConfig =
            SYRFPermissionRequestConfig(
                title = context.getString(R.string.label_request_permission_title),
                message = context.getString(R.string.msg_request_permission_message),
                okButton = context.getString(R.string.action_ok),
                cancelButton = context.getString(R.string.action_cancel)
            )
    }

    /**
     * Builder class that help to create an instance of [SYRFPermissionRequestConfig]
     */
    data class Builder(
        var title: String? = null,
        var message: String? = null,
        var okButton: String? = null,
        var cancelButton: String? = null,
    ) {
        fun title(title: String) = apply { this.title = title }
        fun message(message: String) = apply { this.message = message }
        fun okButton(okButton: String) = apply { this.okButton = okButton }
        fun cancelButton(cancelButton: String) = apply { this.cancelButton = cancelButton }
        fun set(context: Context): SYRFPermissionRequestConfig {

            return SYRFPermissionRequestConfig(
                title ?: context.getString(R.string.label_request_permission_title),
                message ?: context.getString(R.string.msg_request_permission_message),
                okButton ?: context.getString(R.string.action_ok),
                cancelButton ?: context.getString(R.string.action_cancel)
            )
        }
    }
}
