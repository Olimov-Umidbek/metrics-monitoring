package uz.umidbek.metrics.monitoring.central.parser

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import uz.umidbek.commons.enums.SensorType
import uz.umidbek.commons.exceptions.ValidationException
import uz.umidbek.commons.model.ValidationError
import uz.umidbek.metrics.monitoring.central.dto.SensorData

class SensorDataParserTest {
    private val parser = SensorDataParser.INSTANCE
    @Test
    fun toDto() {
        val text = "sensorType=HUMIDITY;sensor_id=t12; value=1234"
        val sensorData: SensorData = parser.toDto(text)
        assertNotNull(sensorData)
        assertEquals("t12", sensorData.sensorId)
        assertEquals(1234, sensorData.value)
        assertEquals(SensorType.HUMIDITY, sensorData.sensorType)
    }

    @Test
    fun `parse failed for value`() {
        val text = "sensorType=TEMPERATURE;sensor_id=t12; value=a1"
        assertThrows(NumberFormatException::class.java) {
            parser.toDto(text)
        }
    }

    @Test
    fun `parse failed for sensor type`() {
        val text = "sensor_id=t12; value=1234"
        val throwable = assertThrows(ValidationException::class.java) {
            parser.toDto(text)
        }
        assertNotNull(throwable)
        assertEquals(ValidationError.EVENT_VALIDATION_ERROR.description, throwable.message)
    }
}