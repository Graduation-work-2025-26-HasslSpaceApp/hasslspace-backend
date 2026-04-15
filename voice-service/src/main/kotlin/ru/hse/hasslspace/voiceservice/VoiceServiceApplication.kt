package ru.hse.hasslspace.voiceservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VoiceServiceApplication

fun main(args: Array<String>) {
    runApplication<VoiceServiceApplication>(*args)
}
