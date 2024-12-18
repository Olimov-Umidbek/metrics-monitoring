package uz.umidbek.warehouse.configs

import ch.qos.logback.classic.ClassicConstants
import org.slf4j.Logger
import uz.umidbek.commons.exceptions.InternalException
import uz.umidbek.commons.model.InternalError
import uz.umidbek.warehouse.configs.props.ApplicationProperties
import uz.umidbek.commons.enums.SensorType
import uz.umidbek.warehouse.utils.Constants.DATAGRAM_PACKET_SIZE
import uz.umidbek.warehouse.utils.Constants.HOST
import uz.umidbek.warehouse.utils.Constants.HUMIDITY_PROPERTY_PREFIX
import uz.umidbek.warehouse.utils.Constants.KAFKA_BOOTSTRAP_SERVER
import uz.umidbek.warehouse.utils.Constants.KAFKA_TOPIC
import uz.umidbek.warehouse.utils.Constants.PORT
import uz.umidbek.warehouse.utils.Constants.TEMPERATURE_PROPERTY_PREFIX
import uz.umidbek.warehouse.utils.getLogger
import java.io.InputStream
import java.util.Properties

class PropertiesConfiguration private constructor() {

    private var properties: Properties
    private var applicationProperties: ApplicationProperties

    init {
        properties = loadProperties()
        applicationProperties = loadApplicationProperties()
    }
    fun getApplicationProperties(): ApplicationProperties = applicationProperties

    fun configureLoggingLevel() {
        val path: String = Thread.currentThread().contextClassLoader
            ?.getResource("logback.xml")?.path
            ?: throw InternalException(InternalError.PROPERTY_LOAD_ERROR)

        System.setProperty(ClassicConstants.AUTOCONFIG_FILE, path)
    }

    private fun loadApplicationProperties(): ApplicationProperties {
        return ApplicationProperties(
            temperatureServerProps = loadServerProps(SensorType.TEMPERATURE),
            humidityServerProps = loadServerProps(SensorType.HUMIDITY),
            kafkaProps = loadKafkaProps(),
            datagramPacketSize = properties.getProperty(DATAGRAM_PACKET_SIZE).toInt()
        )
    }

    private fun loadServerProps(sensorType: SensorType): ApplicationProperties.ServerProps {
        val prefix: String = when(sensorType) {
            SensorType.TEMPERATURE -> TEMPERATURE_PROPERTY_PREFIX
            SensorType.HUMIDITY -> HUMIDITY_PROPERTY_PREFIX
        }

        return ApplicationProperties.ServerProps(
            properties.getProperty("$prefix.$HOST"),
            properties.getProperty("$prefix.$PORT").toInt()
        )
    }

    private fun loadKafkaProps(): ApplicationProperties.KafkaProps {
        return ApplicationProperties.KafkaProps(
            bootstrapServers = properties.getProperty(KAFKA_BOOTSTRAP_SERVER),
            topic = properties.getProperty(KAFKA_TOPIC)
        )
    }

    private fun loadProperties(): Properties {
        try {
            val stream: InputStream = PropertiesConfiguration::class.java.getResourceAsStream("/application.properties")
                ?: throw InternalException(InternalError.PROPERTY_LOAD_ERROR)

            val properties = Properties()
            properties.load(stream)

            properties.forEach { key, value ->
                properties.setProperty(key as String, resolveEnvVars(value as String))
            }

            return properties
        } catch (e: Exception) {
            logger.error("Happened an error while loading properties from the application.properties, message = ${e.message}", e)
            throw e
        }
    }
    private fun resolveEnvVars(value: String): String {
        return value.replace(regex) { match ->
            val (envVar, defaultValue) = match.destructured
            System.getenv().getOrDefault(envVar, defaultValue)
        }
    }

    companion object {
        private val regex = "\\$\\{([^:}]+):([^}]+)}".toRegex()
        private val logger: Logger = getLogger<PropertiesConfiguration>()
        val INSTANCE: PropertiesConfiguration by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { PropertiesConfiguration() }

    }

}