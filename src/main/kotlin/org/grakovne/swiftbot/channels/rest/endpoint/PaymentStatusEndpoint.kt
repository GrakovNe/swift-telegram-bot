package org.grakovne.swiftbot.channels.rest.endpoint

import org.grakovne.swiftbot.channels.rest.UserPaymentService
import org.grakovne.swiftbot.dto.PaymentStatusRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("payment")
class PaymentStatusEndpoint(private val userPaymentService: UserPaymentService) {

    @PostMapping("fetchStatus")
    fun fetchStatus(@RequestBody request: PaymentStatusRequest) = userPaymentService.fetchPaymentStatus(request)
}