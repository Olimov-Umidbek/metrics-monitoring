package uz.umidbek.metrics.monitoring.central

import uz.umidbek.metrics.monitoring.central.config.KafkaConfiguration
import uz.umidbek.metrics.monitoring.central.config.PropertiesConfiguration
import uz.umidbek.metrics.monitoring.central.config.props.ApplicationProperties
import uz.umidbek.metrics.monitoring.central.kafka.ReactiveKafkaConsumer
import uz.umidbek.metrics.monitoring.central.service.MonitoringService

fun main() {
    val propertiesConfiguration = PropertiesConfiguration.INSTANCE
    propertiesConfiguration.configureLoggingLevel()

    val applicationProperties: ApplicationProperties = propertiesConfiguration.getApplicationProperties()
    val monitoringService = MonitoringService(applicationProperties)
    val kafkaConfiguration = KafkaConfiguration.INSTANCE
    val kafkaConsumer = ReactiveKafkaConsumer(kafkaConfiguration, applicationProperties, monitoringService)
    kafkaConsumer.connectToKafka()

    Thread.currentThread().join()
}