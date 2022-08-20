package org.grakovne.swiftbot.endpoint

import org.grakovne.swiftbot.synchronization.SynchronizationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController("/")
class TestEndpoint(private val synchronizationService: SynchronizationService) {

    @GetMapping("{id}")
    fun test(@PathVariable id: UUID) = synchronizationService.fetchPaymentStatus(id)
}