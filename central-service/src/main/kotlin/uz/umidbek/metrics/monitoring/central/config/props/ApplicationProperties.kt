package uz.umidbek.metrics.monitoring.central.config.props

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class ApplicationProperties(
    val topic: String,
    val consumerGroupId: String,
    val bootstrapServers: String,
    val temperatureGroup: List<SensorGroup>,
    val humidityGroup: List<SensorGroup>,
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    class SensorGroup(
        @JsonProperty("sensorId")
        val sensorId: List<String>,
        val threshold: Int
    )
}