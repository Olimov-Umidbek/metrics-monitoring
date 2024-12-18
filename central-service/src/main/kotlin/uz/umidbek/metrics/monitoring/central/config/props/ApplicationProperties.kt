package uz.umidbek.metrics.monitoring.central.config.props

data class ApplicationProperties(
    val topic: String,
    val consumerGroupId: String,
    val bootstrapServers: String,
    val humidityThreshold: Int,
    val temperatureThreshold: Int,
)