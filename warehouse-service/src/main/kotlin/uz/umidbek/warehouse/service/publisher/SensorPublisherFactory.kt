package uz.umidbek.warehouse.service.publisher

import org.apache.kafka.clients.producer.KafkaProducer
import uz.umidbek.warehouse.configs.KafkaConfiguration
import uz.umidbek.warehouse.configs.PropertiesConfiguration
import uz.umidbek.commons.enums.SensorType
import uz.umidbek.warehouse.service.publisher.impl.HumiditySensorPublisher
import uz.umidbek.warehouse.service.publisher.impl.TemperatureSensorPublisher

class SensorPublisherFactory {

    private val kafkaProducer: KafkaProducer<String, String> = KafkaConfiguration.INSTANCE.kafkaProducer()
    private val applicationProperties = PropertiesConfiguration.INSTANCE.getApplicationProperties()
    fun buildSensorListener(sensorType: SensorType): SensorPublisher {
        return when(sensorType) {
            SensorType.TEMPERATURE -> TemperatureSensorPublisher(applicationProperties, kafkaProducer)
            SensorType.HUMIDITY -> HumiditySensorPublisher(applicationProperties, kafkaProducer)
        }
    }
}