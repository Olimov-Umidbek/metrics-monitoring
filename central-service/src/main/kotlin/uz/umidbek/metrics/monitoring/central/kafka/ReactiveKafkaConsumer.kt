package uz.umidbek.metrics.monitoring.central.kafka

import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import reactor.core.Disposable
import reactor.core.publisher.ConnectableFlux
import reactor.kafka.receiver.ReceiverRecord
import uz.umidbek.metrics.monitoring.central.config.props.ApplicationProperties
import uz.umidbek.metrics.monitoring.central.utils.getLogger
import uz.umidbek.metrics.monitoring.central.config.KafkaConfiguration
import uz.umidbek.metrics.monitoring.central.parser.SensorDataParser
import uz.umidbek.metrics.monitoring.central.service.MonitoringService
import java.util.concurrent.CancellationException
import java.util.concurrent.CountDownLatch

class ReactiveKafkaConsumer(
    private val kafkaConfiguration: KafkaConfiguration,
    private val applicationProperties: ApplicationProperties,
    private val monitoringService: MonitoringService,
    private val mapper: SensorDataParser = SensorDataParser.INSTANCE
) {
    @Volatile
    private var kafkaConnection: Pair<Disposable, CountDownLatch>? = null

    fun connectToKafka() {
        logger.info("Attempt to connect to Kafka topic \"${applicationProperties.topic}\"")

        val latch = CountDownLatch(1)
        val kafkaFlux: ConnectableFlux<ReceiverRecord<String, String>> = kafkaConfiguration.kafkaReceiver()
        kafkaFlux
            .flatMap {record ->
                mono {
                    processRecord(record)
                }.doOnSuccess {
                    record.receiverOffset().commit()
                        .doOnError { logger.error("Happened error while committing record, offset = ${record.offset()}") }
                        .subscribe()
                }
            }
            .subscribe (
                {},
                { e ->
                    when(e) {
                        is CancellationException -> {
                            logger.info("Received cancellation signal from Kafka consumer: ${e.message}")
                            latch.countDown()
                        }
                        else -> logger.error("Received error signal from Kafka consumer", e)
                    }
                },
                {
                    logger.info("Received complete signal from Kafka consumer")
                    latch.countDown()
                }
            )

        logger.info("Successfully connected to Kafka topic \"${applicationProperties.topic}\"")
        kafkaConnection = kafkaFlux.connect() to latch
    }

    private fun disconnectFromKafka() {
        logger.info("Attempt to disconnect from Kafka topic \"${applicationProperties.topic}\"")
        val connection: Pair<Disposable, CountDownLatch>? = kafkaConnection
        if (connection == null) {
            logger.warn(
                "Can't disconnect from Kafka topic \"${applicationProperties.topic}\": " +
                    "there is no active connections"
            )
            return
        }

        connection.first.dispose()
        connection.second.await()
        kafkaConnection = null
        logger.info("Successfully disconnected from Kafka topic \"${applicationProperties.topic}\"")
    }

    private fun processRecord(record: ReceiverRecord<String, String>) {
        runCatching {
            mapper.toDto(record.value())
        }.onFailure {
            logger.warn(it.message, it)
        }.onSuccess {
            monitoringService.process(it)
        }
    }

    companion object {
        private val logger: Logger = getLogger<ReactiveKafkaConsumer>()
    }
}