package com.egco.mailbot.config

import org.springframework.context.annotation.Configuration

@Configuration
class StatusConfig {
    val WAIT = "wait"
    val CALLING = "call"
    val WAIT_FOR_SENDER = "waitForSender"
    val VERIFY_SENDER = "verifySender"
    val WAIT_FOR_PACKING = "waitForPacking"
    val SENDING = "sending"
    val WAIT_FOR_TARGET = "waitForTarget"
    val VERIFY_TARGET = "verifyTarget"
    val WAIT_FOR_PICK_UP = "waitForPickUp"
    val DONE = "done"
    val RETURNING = "returning"
    val WAIT_FOR_RETURNING = "waitForReturning"
    val VERIFY_RETURN_SENDER = "verifyReturnSender"
    val WAIT_FOR_TURNING = "waitForTurning"
    val FAILED = "failed"
    val CALL_ADMIN = "callAdmin"
}