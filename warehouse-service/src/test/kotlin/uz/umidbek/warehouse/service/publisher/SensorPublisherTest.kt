package uz.umidbek.warehouse.service.publisher

import org.apache.kafka.clients.producer.KafkaProducer
import org.junit.jupiter.api.Test

import org.mockito.Mockito.*
import uz.umidbek.warehouse.configs.props.ApplicationProperties
import uz.umidbek.warehouse.service.publisher.impl.TemperatureSensorPublisher
import java.nio.ByteBuffer

class SensorPublisherTest {
    private val applicationProperties = mock<ApplicationProperties>()
    private val kafkaProps = mock<ApplicationProperties.KafkaProps>()
    private val kafkaProducer = mock<KafkaProducer<String, String>>()
    private val publisher: SensorPublisher = TemperatureSensorPublisher(applicationProperties, kafkaProducer)

    @Test
    fun publish() {
        `when`(kafkaProps.topic).thenReturn("topic")
        `when`(kafkaProps.bootstrapServers).thenReturn("bootstrapServer")
        `when`(applicationProperties.kafkaProps).thenReturn(kafkaProps)
        val data: ByteArray = "test0".toByteArray().plus(ByteBuffer.allocate(1024).array())

        publisher.publish(data)
        verify(kafkaProducer, times(1)).send(any(), any())
    }

    @Test
    fun sensorType() {
    }
}