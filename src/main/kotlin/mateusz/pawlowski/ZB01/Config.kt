package mateusz.pawlowski.ZB01

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.client.RestTemplate

@Configuration
class Config {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @PostConstruct
    fun init() {
        objectMapper.dateFormat = ISO8601DateFormat()
    }


    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}