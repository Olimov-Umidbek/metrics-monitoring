package uz.umidbek.warehouse.configs

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.RoundRobinPartitioner
import org.apache.kafka.common.serialization.StringSerializer
import uz.umidbek.warehouse.configs.props.ApplicationProperties
import java.util.Properties

class KafkaConfiguration private constructor (
    private val applicationProperties: ApplicationProperties
) {

    fun kafkaProducer(): KafkaProducer<String, String> {
        val properties = Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperties.kafkaProps.bootstrapServers)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.ACKS_CONFIG, "all")
            put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner::class.java)
        }

        return KafkaProducer(properties)
    }

    companion object {
        private val applicationProperties = PropertiesConfiguration.INSTANCE.getApplicationProperties()
        val INSTANCE: KafkaConfiguration by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            KafkaConfiguration(applicationProperties)
        }
    }
}