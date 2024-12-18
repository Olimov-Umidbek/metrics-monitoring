package uz.umidbek.metrics.monitoring.central.config

import ch.qos.logback.classic.ClassicConstants
import org.slf4j.Logger
import uz.umidbek.commons.exceptions.InternalException
import uz.umidbek.commons.model.InternalError
import uz.umidbek.metrics.monitoring.central.config.props.ApplicationProperties
import uz.umidbek.metrics.monitoring.central.utils.Constants.BOOTSTRAP_ADDRESS
import uz.umidbek.metrics.monitoring.central.utils.Constants.CONSUMER_GROUP
import uz.umidbek.metrics.monitoring.central.utils.Constants.HUMIDITY_THRESHOLD
import uz.umidbek.metrics.monitoring.central.utils.Constants.TEMPERATURE_THRESHOLD
import uz.umidbek.metrics.monitoring.central.utils.Constants.TOPIC
import uz.umidbek.metrics.monitoring.central.utils.getLogger
import java.io.InputStream
import java.util.*

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
            ?.getResource("logback.xml")?.file?.toString()
            ?: throw InternalException(InternalError.PROPERTY_LOAD_ERROR)

        System.setProperty(ClassicConstants.AUTOCONFIG_FILE, path)
    }

    private fun loadApplicationProperties(): ApplicationProperties {
        return ApplicationProperties(
            topic = properties.getProperty(TOPIC),
            consumerGroupId = properties.getProperty(CONSUMER_GROUP),
            bootstrapServers = properties.getProperty(BOOTSTRAP_ADDRESS),
            temperatureThreshold = properties.getProperty(TEMPERATURE_THRESHOLD).toInt(),
            humidityThreshold = properties.getProperty(HUMIDITY_THRESHOLD).toInt(),
        )
    }

    private fun loadProperties(): Properties {
        try {
            val stream: InputStream = PropertiesConfiguration::class.java
                .getResourceAsStream("/application.properties")
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