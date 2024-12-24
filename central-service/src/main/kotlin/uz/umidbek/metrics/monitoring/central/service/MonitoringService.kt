package uz.umidbek.metrics.monitoring.central.service

import org.slf4j.Logger
import uz.umidbek.commons.enums.SensorType
import uz.umidbek.commons.exceptions.InternalException
import uz.umidbek.commons.model.InternalError
import uz.umidbek.metrics.monitoring.central.config.props.ApplicationProperties
import uz.umidbek.metrics.monitoring.central.dto.SensorData
import uz.umidbek.metrics.monitoring.central.utils.getLogger

class MonitoringService(
    applicationProperties: ApplicationProperties
) {

    private val humidityGroup: List<ApplicationProperties.SensorGroup> = applicationProperties.humidityGroup
    private val temperatureGroup: List<ApplicationProperties.SensorGroup> = applicationProperties.temperatureGroup

    fun process(data: SensorData) {
        val threshold = getThreshold(data)

        if (threshold < data.value) {
            logger.error("ALARM!!! The incoming threshold=${data.value} of " +
                    "sensor[${data.sensorType}]=${data.sensorId}'s" + " is bigger than ${threshold}..."
            )
        }
    }


    private fun getThreshold(data: SensorData): Int {
        return when(data.sensorType) {
            SensorType.TEMPERATURE -> temperatureGroup.firstOrNull { it.sensorId.contains(data.sensorId) }?.threshold
            SensorType.HUMIDITY -> humidityGroup.firstOrNull { it.sensorId.contains(data.sensorId) }?.threshold
        } ?: throw InternalException(InternalError.SENSOR_NOT_FOUND)
    }
    companion object {
        private val logger: Logger = getLogger<MonitoringService>()
    }
}