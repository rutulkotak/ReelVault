package com.reelvault.app.utils

expect class VaultTime() {
    fun getCurrentEpochMillis(): Long
    fun getFormattedDate(): String
}
