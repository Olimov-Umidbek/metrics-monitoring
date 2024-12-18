package uz.umidbek.warehouse.configs.props

data class ApplicationProperties(
    val temperatureServerProps: ServerProps,
    val humidityServerProps: ServerProps,
    val kafkaProps: KafkaProps,
    val datagramPacketSize: Int
) {
    data class ServerProps(
        val host: String,
        val port: Int
    )

    data class KafkaProps (
        val bootstrapServers: String,
        val topic: String
    )
}

