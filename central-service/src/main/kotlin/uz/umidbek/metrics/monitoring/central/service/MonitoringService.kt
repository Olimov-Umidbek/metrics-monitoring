package uz.umidbek.metrics.monitoring.central.service

import org.slf4j.Logger
import uz.umidbek.commons.enums.SensorType
import uz.umidbek.metrics.monitoring.central.config.props.ApplicationProperties
import uz.umidbek.metrics.monitoring.central.dto.SensorData
import uz.umidbek.metrics.monitoring.central.utils.getLogger

class MonitoringService(
    private val applicationProperties: ApplicationProperties
) {

    fun process(data: SensorData) {
        val threshold = when(data.sensorType) {
            SensorType.TEMPERATURE -> applicationProperties.temperatureThreshold
            SensorType.HUMIDITY -> applicationProperties.humidityThreshold
        }

        if (threshold < data.value) {
            logger.error("ALARM!!! The incoming threshold=${data.value} of " +
                    "sensor[${data.sensorType}]=${data.sensorId}'s" + " is bigger than ${threshold}..."
            )
        }
    }

    companion object {
        private val logger: Logger = getLogger<MonitoringService>()
    }
}