package uz.umidbek.warehouse.service.server

import uz.umidbek.commons.exceptions.InternalException
import uz.umidbek.commons.model.InternalError
import uz.umidbek.warehouse.configs.props.ApplicationProperties
import uz.umidbek.commons.enums.SensorType
import uz.umidbek.warehouse.service.publisher.SensorPublisherFactory
import uz.umidbek.warehouse.service.server.impl.HumiditySensorServer
import uz.umidbek.warehouse.service.server.impl.TemperatureSensorServer
import java.util.concurrent.ConcurrentHashMap

class SensorServerFactory (
    applicationProperties: ApplicationProperties,
    sensorPublisherFactory: SensorPublisherFactory,
) {

    private val map: ConcurrentHashMap<SensorType, SensorServer> = ConcurrentHashMap<SensorType, SensorServer>()

    init {
        val temperatureSensorServer = TemperatureSensorServer(
            applicationProperties,
            sensorPublisherFactory.buildSensorListener(SensorType.TEMPERATURE)
        )
        val humiditySensorServer = HumiditySensorServer(
            applicationProperties,
            sensorPublisherFactory.buildSensorListener(SensorType.HUMIDITY)
        )
        map[temperatureSensorServer.getSensorType()] = temperatureSensorServer
        map[humiditySensorServer.getSensorType()] = humiditySensorServer
    }

    fun getSensorServiceBy(sensorType: SensorType): SensorServer {
        return map[sensorType]
            ?: throw InternalException(InternalError.UNKNOWN_TYPE_OF_SENSOR)
    }
}