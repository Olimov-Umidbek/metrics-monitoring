package uz.umidbek.warehouse

import uz.umidbek.warehouse.configs.PropertiesConfiguration
import uz.umidbek.warehouse.configs.props.ApplicationProperties
import uz.umidbek.commons.enums.SensorType
import uz.umidbek.warehouse.service.publisher.SensorPublisherFactory
import uz.umidbek.warehouse.service.server.SensorServerFactory

fun main() {
    PropertiesConfiguration.INSTANCE.configureLoggingLevel()
    val properties: ApplicationProperties = PropertiesConfiguration.INSTANCE.getApplicationProperties()
    val publisherFactory = SensorPublisherFactory()
    val sensorServerFactory = SensorServerFactory(
        applicationProperties = properties,
        sensorPublisherFactory = publisherFactory
    )
    val humidityServer = sensorServerFactory.getSensorServiceBy(SensorType.HUMIDITY)
    val temperatureSensorServer = sensorServerFactory.getSensorServiceBy(SensorType.TEMPERATURE)

    humidityServer.startServer()
    temperatureSensorServer.startServer()

    Thread.currentThread().join()
}
