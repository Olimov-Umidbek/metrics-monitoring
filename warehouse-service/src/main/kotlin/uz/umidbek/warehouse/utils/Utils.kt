package uz.umidbek.warehouse.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun ByteArray.trimAndConvertToString(): String {
    return String(
        this.copyOf(this.indexOfFirst { it == (0).toByte() })
    )
}
inline fun <reified T> getLogger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}
