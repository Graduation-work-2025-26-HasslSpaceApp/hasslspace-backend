package ru.hse.hasslspace.userservice.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import ru.hse.hasslspace.userservice.dto.EmailRequest

@Configuration
@ConditionalOnProperty(
    name = ["notification-service.transport-type"],
    havingValue = "KAFKA",
    matchIfMissing = false
)
class KafkaConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String
) {

    @Bean
    fun kafkaProducerConfigs(): Map<String, Any> = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        // use FQCN string to avoid direct reference to deprecated class in code
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to "org.springframework.kafka.support.serializer.JsonSerializer"
    )

    @Bean
    fun producerFactory(): ProducerFactory<String, EmailRequest> =
        DefaultKafkaProducerFactory(kafkaProducerConfigs())

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, EmailRequest> =
        KafkaTemplate(producerFactory())
}
