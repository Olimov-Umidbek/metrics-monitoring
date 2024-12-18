package uz.umidbek.commons.model

enum class InternalError(val message: String) {
    UNKNOWN_TYPE_OF_SENSOR("Unknown type of sensor"),
    PROPERTY_LOAD_ERROR("Properties file not found"),
    SOCKET_READING_ERROR("Happened an error while reading socket")
}