package uz.umidbek.warehouse.service.publisher

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import uz.umidbek.warehouse.configs.props.ApplicationProperties
import uz.umidbek.commons.enums.SensorType
import uz.umidbek.warehouse.utils.getLogger
import uz.umidbek.warehouse.utils.trimAndConvertToString

abstract class SensorPublisher(
    private val applicationProperties: ApplicationProperties,
    private val kafkaProducer: KafkaProducer<String, String>,
) {
    fun publish(data: ByteArray) {
       val record = ProducerRecord<String, String>(
           applicationProperties.kafkaProps.topic,
           buildPayload(data)
       )

        kafkaProducer.send(record) { metadata, exception ->
            exception?.let {
                logger.error("Error sending message: ${exception.message}", exception)
            }
            metadata?.let {
                logger.debug("Message sent: ${metadata.topic()} - Partition: ${metadata.partition()} - Offset: ${metadata.offset()}")
            }
        }
    }

    private fun buildPayload(data: ByteArray): String {
        val sb = StringBuilder()
        sb.append("sensorType=${sensorType().name};")
        sb.append(data.trimAndConvertToString())
        return sb.toString()
    }

    abstract fun sensorType(): SensorType

    companion object {
        private val logger: Logger = getLogger<SensorPublisher>()
    }
}