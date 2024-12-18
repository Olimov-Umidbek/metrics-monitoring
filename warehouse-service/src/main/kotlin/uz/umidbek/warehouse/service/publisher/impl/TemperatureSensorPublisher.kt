package uz.umidbek.warehouse.service.publisher.impl

import org.apache.kafka.clients.producer.KafkaProducer
import uz.umidbek.warehouse.configs.props.ApplicationProperties
import uz.umidbek.commons.enums.SensorType
import uz.umidbek.warehouse.service.publisher.SensorPublisher

class TemperatureSensorPublisher(
    applicationProperties: ApplicationProperties,
    kafkaProducer: KafkaProducer<String, String>
): SensorPublisher(
    applicationProperties,
    kafkaProducer
) {

    override fun sensorType() = SensorType.TEMPERATURE
}