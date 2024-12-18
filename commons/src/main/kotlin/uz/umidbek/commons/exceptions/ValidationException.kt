package uz.umidbek.commons.exceptions

import uz.umidbek.commons.model.ValidationError

class ValidationException(
    error: ValidationError,
    throwable: Throwable? = null
): BaseException(
    error.description,
    throwable
)