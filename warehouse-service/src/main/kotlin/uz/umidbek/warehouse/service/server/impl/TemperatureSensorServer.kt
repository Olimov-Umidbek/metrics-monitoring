package uz.umidbek.warehouse.service.server.impl

import uz.umidbek.warehouse.configs.props.ApplicationProperties
import uz.umidbek.commons.enums.SensorType
import uz.umidbek.warehouse.service.publisher.SensorPublisher
import java.net.DatagramSocket

class TemperatureSensorServer(
    applicationProperties: ApplicationProperties,
    publisher: SensorPublisher
): BaseSensorServer(
    applicationProperties,
    publisher
) {
    override fun configureServer(): DatagramSocket {
        return DatagramSocket(getSensorServerProps().port)
    }

    override fun getSensorType(): SensorType = SensorType.TEMPERATURE
}