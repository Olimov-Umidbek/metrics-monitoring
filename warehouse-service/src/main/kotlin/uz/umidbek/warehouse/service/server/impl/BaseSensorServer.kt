package uz.umidbek.warehouse.service.server.impl

import org.slf4j.Logger
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import uz.umidbek.warehouse.utils.getLogger
import uz.umidbek.commons.enums.SensorType
import uz.umidbek.commons.exceptions.InternalException
import uz.umidbek.commons.model.InternalError
import uz.umidbek.warehouse.configs.props.ApplicationProperties
import uz.umidbek.warehouse.service.server.SensorServer
import uz.umidbek.warehouse.service.publisher.SensorPublisher
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.nio.ByteBuffer

abstract class BaseSensorServer(
    private val applicationProperties: ApplicationProperties,
    private val publisher: SensorPublisher,
): SensorServer {

    override fun startServer() {
        val receiverFlux: Flux<DatagramPacket> = Flux.create { sink ->
            val socket: DatagramSocket = configureServer()
            logger.info("The ${getSensorType()} server started...")
            try {
                while (!sink.isCancelled) {
                    val byteArray = ByteBuffer.allocate(applicationProperties.datagramPacketSize)
                    val packet = DatagramPacket(byteArray.array(), byteArray.limit())
                    socket.receive(packet)
                    sink.next(packet)
                }
            } catch (e: Exception) {
                logger.error("Happened error while reading socket, message=${e.message}", e)
                throw InternalException(error = InternalError.SOCKET_READING_ERROR, e)
            } finally {
                socket.close()
                logger.info("Socket server disconnected...")
            }
        }.subscribeOn(Schedulers.boundedElastic())

        receiverFlux.subscribe(
            { publisher.publish(it.data) },
            { error -> logger.error("Error occurred: ${error.message}", error) },
            { logger.info("Server stream completed.") }
        )
    }

    protected fun getSensorServerProps(): ApplicationProperties.ServerProps {
        return when (getSensorType()) {
            SensorType.TEMPERATURE -> applicationProperties.temperatureServerProps
            SensorType.HUMIDITY -> applicationProperties.humidityServerProps
        }
    }

    abstract fun configureServer(): DatagramSocket

    abstract fun getSensorType(): SensorType

    companion object {
        private val logger: Logger = getLogger<BaseSensorServer>()
    }
}