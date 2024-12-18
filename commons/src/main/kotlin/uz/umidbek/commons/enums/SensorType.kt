package uz.umidbek.commons.enums

enum class SensorType {
    TEMPERATURE,
    HUMIDITY;

    companion object {
        fun fromName(value: String): SensorType {
            return entries.first { it.name == value }
        }
    }
}