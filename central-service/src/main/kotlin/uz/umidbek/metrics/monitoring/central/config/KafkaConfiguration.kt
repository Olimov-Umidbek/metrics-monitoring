package uz.umidbek.metrics.monitoring.central.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import reactor.core.publisher.ConnectableFlux
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.receiver.ReceiverRecord
import uz.umidbek.metrics.monitoring.central.config.props.ApplicationProperties
import uz.umidbek.metrics.monitoring.central.utils.getLogger
import java.util.*

class KafkaConfiguration private constructor (
    private val applicationProperties: ApplicationProperties
) {

    fun kafkaReceiver(): ConnectableFlux<ReceiverRecord<String, String>> {
        val properties = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperties.bootstrapServers)
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.GROUP_ID_CONFIG, applicationProperties.consumerGroupId)
        }

        val receiverOptions = ReceiverOptions.create<String, String>(properties)
            .subscription(listOf(applicationProperties.topic))
            .addAssignListener { partitions ->
                partitions.forEach { partition ->
                    logger.info("Assigned partition: ${partition.topicPartition()}-${partition.topicPartition()}")
                }
            }
            .addRevokeListener { partitions ->
                partitions.forEach { partition ->
                    logger.info("Revoked partition: ${partition.topicPartition()}-${partition.topicPartition()}")
                }
            }

        return KafkaReceiver
            .create(receiverOptions)
            .receive()
            .publish()
    }

    companion object {
        private val properties: ApplicationProperties = PropertiesConfiguration.INSTANCE.getApplicationProperties()
        val INSTANCE: KafkaConfiguration by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { KafkaConfiguration(properties) }

        private val logger: Logger = getLogger<KafkaConfiguration>()
    }
}