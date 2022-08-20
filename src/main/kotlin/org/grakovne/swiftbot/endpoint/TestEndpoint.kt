package org.grakovne.swiftbot.endpoint

import org.grakovne.swiftbot.synchronization.PaymentSynchronizationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController("/")
class TestEndpoint(private val synchronizationService: PaymentSynchronizationService) {

    @GetMapping("{id}")
    fun test(@PathVariable id: UUID) = synchronizationService.fetchStatus(id)
}