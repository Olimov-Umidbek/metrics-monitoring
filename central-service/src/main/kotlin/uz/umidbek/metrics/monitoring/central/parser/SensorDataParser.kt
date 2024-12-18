package uz.umidbek.metrics.monitoring.central.parser

import uz.umidbek.commons.enums.SensorType
import uz.umidbek.commons.exceptions.ValidationException
import uz.umidbek.commons.model.ValidationError
import uz.umidbek.metrics.monitoring.central.dto.SensorData

class SensorDataParser private constructor() {
    fun toDto(data: String): SensorData {
        val (sensorType, sensorId, value) = validateAndExtract(data)

        return SensorData(
            sensorType = SensorType.fromName(sensorType),
            sensorId = sensorId,
            value = value.toInt()
        )
    }

    private fun validateAndExtract(data: String): MatchResult.Destructured {
        val match: MatchResult = regex.find(data.trim())
            ?: throw ValidationException(ValidationError.EVENT_VALIDATION_ERROR)

        return match.destructured
    }

    companion object {
        val regex = "sensorType=(.*.);sensor_id=(.*.);\\s+value=([0-9]*)".toRegex()
        val INSTANCE: SensorDataParser by lazy (LazyThreadSafetyMode.SYNCHRONIZED) { SensorDataParser() }
    }

}