package uz.umidbek.warehouse.service.server.impl

import org.apache.kafka.clients.producer.KafkaProducer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import uz.umidbek.warehouse.configs.props.ApplicationProperties
import uz.umidbek.warehouse.service.publisher.SensorPublisher
import uz.umidbek.warehouse.service.publisher.impl.HumiditySensorPublisher
import uz.umidbek.warehouse.service.server.SensorServer
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class HumiditySensorServerTest {
    private lateinit var sensorServer: SensorServer
    private lateinit var publisher: SensorPublisher
    private val kafkaProps = mock<ApplicationProperties.KafkaProps>()
    private val kafkaProducer: KafkaProducer<String, String> = mock()
    private val applicationProperties = mock<ApplicationProperties>()
    @BeforeEach
    fun before() {
        val humidityProps = ApplicationProperties.ServerProps("localhost", 3344)
        `when`(applicationProperties.humidityServerProps).thenReturn(humidityProps)
        `when`(applicationProperties.datagramPacketSize).thenReturn(16)

        `when`(kafkaProps.topic).thenReturn("topic")
        `when`(kafkaProps.bootstrapServers).thenReturn("bootstrapServer")
        `when`(applicationProperties.kafkaProps).thenReturn(kafkaProps)

        publisher = HumiditySensorPublisher(applicationProperties, kafkaProducer)
        sensorServer = HumiditySensorServer(applicationProperties, publisher)
    }

    @Test
    fun startServer() {
        sensorServer.startServer()
        connectAndSend()
    }

    private fun connectAndSend() {
        DatagramSocket().use { datagramSocket ->
            packetList
                .map { it.toByteArray() }
                .map { DatagramPacket(it, it.size, InetAddress.getByName("localhost"), 3344) }
                .forEach { datagramSocket.send(it) }
        }
    }

    companion object {
        private val packetList = listOf("message1", "message2", "message3")
    }
}