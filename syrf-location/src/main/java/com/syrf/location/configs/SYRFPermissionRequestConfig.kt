package com.syrf.location.configs

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
    @StringRes val title: Int,
    @StringRes val message: Int,
    @StringRes val okButton: Int,
    @StringRes val cancelButton: Int,
) {

    companion object {
        /**
         * Provide a default config for using in cases client init the SDK
         * without config or missing some properties in config
         */
        val DEFAULT: SYRFPermissionRequestConfig =
            SYRFPermissionRequestConfig(
                title = R.string.label_request_permission_title,
                message = R.string.msg_request_permission_message,
                okButton = R.string.action_ok,
                cancelButton = R.string.action_cancel
            )
    }

    /**
     * Builder class that help to create an instance of [SYRFPermissionRequestConfig]
     */
    data class Builder(
        @StringRes var title: Int? = null,
        @StringRes var message: Int? = null,
        @StringRes var okButton: Int? = null,
        @StringRes var cancelButton: Int? = null,
    ) {
        fun title(@StringRes title: Int) = apply { this.title = title }
        fun message(@StringRes message: Int) = apply { this.message = message }
        fun okButton(@StringRes okButton: Int) = apply { this.okButton = okButton }
        fun cancelButton(@StringRes cancelButton: Int) = apply { this.cancelButton = cancelButton }
        fun set() = SYRFPermissionRequestConfig(
            title ?: DEFAULT.title,
            message ?: DEFAULT.message,
            okButton ?: DEFAULT.okButton,
            cancelButton ?: DEFAULT.cancelButton
        )
    }
}
