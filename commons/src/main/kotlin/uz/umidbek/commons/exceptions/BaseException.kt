package uz.umidbek.commons.exceptions

open class BaseException(
    message: String,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)