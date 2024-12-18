package uz.umidbek.commons.exceptions

import uz.umidbek.commons.model.InternalError

class InternalException(error: InternalError, throwable: Throwable? = null) : BaseException(error.message, throwable)