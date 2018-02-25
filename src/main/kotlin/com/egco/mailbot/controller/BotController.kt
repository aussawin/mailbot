package com.egco.mailbot.controller

import com.egco.mailbot.config.FirebaseConfig
import com.egco.mailbot.config.RaspConfig
import com.egco.mailbot.config.StatusConfig
import com.egco.mailbot.dao.*
import com.egco.mailbot.domain.BotPosition
import com.egco.mailbot.domain.Log
import com.egco.mailbot.exception.RaspStatusError
import com.egco.mailbot.exception.StatusNotFoundException
import com.egco.mailbot.repository.BotPositionRepository
import com.egco.mailbot.repository.LogRepository
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping(value = "/api/botController")
@CrossOrigin("*")
class BotController(val logRepository: LogRepository,
                    val botPositionRepository: BotPositionRepository,
                    val raspConfig: RaspConfig) {

    private val status = StatusConfig()
    private var count: Int = 0
    private val restTemplate = RestTemplate()
    private val firebaseConfig = FirebaseConfig()

    @RequestMapping(value = "/setPosition", method = arrayOf(RequestMethod.PATCH))
    fun setPosition(@RequestBody setCurrentPosition: SetCurrentPosition){
        var current = botPositionRepository.findByState("current")
        print("PREV. CURRENT = " + current + "\n")
        current.position = setCurrentPosition.message
        print("SET LOCATION : " + current.position +"\n")
        print("NOW CURRENT = " + current + "\n")
        botPositionRepository.save(current)
    }

    @RequestMapping(value = "/changeStatus", method = arrayOf(RequestMethod.PATCH))
    fun changeStatus() {
        // ALL SENDING METHODS //
        //Calling + Sending + Returning
        var current = botPositionRepository.findByState("current")
        if (logRepository.findByStatus(current.status) != null){
            var log = logRepository.findByStatus(current.status)!!
            val target = log.target
            val sender = log.sender

            current.status = when (log.status){
                status.CALLING -> {
                    FirebaseController().send(sender, "Robot is ready to receive message from you!", firebaseConfig.MESSAGE_PATH)
                    val res: String = restTemplate.getForObject(raspConfig.COUNTDOWN, String::class.java)
                    resIsOK(res)
                    status.WAIT_FOR_SENDER
                }
                status.SENDING -> {
                    FirebaseController().send(target, "Robot is ready to sending message to you!", firebaseConfig.MESSAGE_PATH)
                    val res: String = restTemplate.getForObject(raspConfig.COUNTDOWN, String::class.java)
                    resIsOK(res)
                    status.WAIT_FOR_TARGET
                }
                status.RETURNING -> {
                    FirebaseController().send(sender, "Robot is ready to return message to you!", firebaseConfig.MESSAGE_PATH)
                    val res: String = restTemplate.getForObject(raspConfig.COUNTDOWN, String::class.java)
                    resIsOK(res)
                    status.WAIT_FOR_RETURNING
                }
                else -> {
                    throw StatusNotFoundException(status = log.status)
                }
            }
            log.status = current.status
            logRepository.save(log)
            botPositionRepository.save(current)
        }
        else{
            throw StatusNotFoundException(status = current.status)
        }
    }

    @RequestMapping(value = "/pressing", method = arrayOf(RequestMethod.PATCH))
    fun pressMethods(){
        // ALL WAITING METHODS //
        //WaitForSender + WaitForTarget + WaitForPacking + WaitForPickup
        var current = botPositionRepository.findByState("current")
        if (logRepository.findByStatus(current.status) != null){
            var log = logRepository.findByStatus(current.status)!!
            current.status = when (log.status){
                status.WAIT_FOR_SENDER -> {
                    // OPEN CAMERA //
                    val res: String = restTemplate.getForObject(raspConfig.OPENCAM, String::class.java)
                    resIsOK(res)
                    status.VERIFY_SENDER
                }
                status.WAIT_FOR_TARGET -> {
                    // OPEN CAMERA //
                    val res: String = restTemplate.getForObject(raspConfig.OPENCAM, String::class.java)
                    resIsOK(res)
                    status.VERIFY_TARGET
                }
                status.WAIT_FOR_PACKING -> {
                    // CALLING //
                    val location = Location(current.position, log.targetLocation)
                    val res: String = restTemplate.postForObject(raspConfig.CALL, location, String::class.java)
                    resIsOK(res)
                    status.SENDING
                }
                status.WAIT_FOR_PICK_UP -> {
                    FirebaseController().send(log.sender, "The message is sending to ${log.target} correctly.", firebaseConfig.MESSAGE_PATH)
                    status.DONE
                }
                status.WAIT_FOR_RETURNING -> {
                    val res: String = restTemplate.getForObject(raspConfig.OPENCAM, String::class.java)
                    resIsOK(res)
                    status.VERIFY_RETURN_SENDER
                }
                else -> throw StatusNotFoundException(status = log.status)
            }
            log.status = current.status
            botPositionRepository.save(current)
            logRepository.save(log)
            if (log.status == status.DONE){
                checkNextQueue(current, log)
            }
        }
        else throw StatusNotFoundException(status = current.status)
    }

    @RequestMapping(value = "/sendFaceName", method = arrayOf(RequestMethod.PATCH))
    fun isUser(@RequestBody req: FaceRequest) {
        // ALL VERIFY METHODS //
        // VerifySender + VerifyTarget
        print("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX ${req.message} XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX \n")
        var current = botPositionRepository.findByState("current")
        if (logRepository.findByStatus(current.status) != null) {
            var log = logRepository.findByStatus(current.status)!!
            val target = log.target
            val sender = log.sender
            current.status = when (log.status){
                status.VERIFY_SENDER -> {
                    if (req.message == sender){
                        // UNLOCK //
                        val res: String = restTemplate.getForObject(raspConfig.UNLOCK, String::class.java)
                        resIsOK(res)
                        val res2: String = restTemplate.getForObject(raspConfig.WAIT_FOR_PRESS, String::class.java)
                        resIsOK(res2)
                        count = 0
                        status.WAIT_FOR_SENDER
                    }
                    else {
                        // OPEN CAMERA AGAIN //
                        val res: String = restTemplate.getForObject(raspConfig.OPENCAM, String::class.java)
                        resIsOK(res)
                        count ++
                        FirebaseController().send(sender, count.toString(), firebaseConfig.VERIFY_PATH)
                        if (count == 3) {
                            checkNextQueue(current, log)
                            status.FAILED
                        }
                        else status.VERIFY_SENDER
                    }
                }
                status.VERIFY_TARGET -> {
                    if (req.message == target){
                        // UNLOCK //
                        val res: String = restTemplate.getForObject(raspConfig.UNLOCK, String::class.java)
                        resIsOK(res)
                        val res2: String = restTemplate.getForObject(raspConfig.WAIT_FOR_PRESS, String::class.java)
                        resIsOK(res2)
                        count = 0
                        status.WAIT_FOR_PICK_UP
                    }
                    else {
                        // OPEN CAMERA AGAIN //
                        val res: String = restTemplate.getForObject(raspConfig.OPENCAM, String::class.java)
                        resIsOK(res)
                        count ++
                        FirebaseController().send(target, count.toString(), firebaseConfig.VERIFY_PATH)
                        if (count == 3) {
                            val location = Location(current.position, log.senderLocation)
                            val res2: String = restTemplate.postForObject(raspConfig.CALL, location, String::class.java)
                            resIsOK(res2)
                            status.RETURNING
                        }
                        else status.VERIFY_TARGET
                    }
                }
                status.VERIFY_RETURN_SENDER -> {
                    if (req.message == sender){
                        val res: String = restTemplate.getForObject(raspConfig.UNLOCK, String::class.java)
                        resIsOK(res)
                        val res2: String = restTemplate.getForObject(raspConfig.WAIT_FOR_PRESS, String::class.java)
                        resIsOK(res2)
                        count = 0
                        status.WAIT_FOR_TURNING
                    }
                    else {
                        // OPEN CAMERA AGAIN //
                        val res: String = restTemplate.getForObject(raspConfig.OPENCAM, String::class.java)
                        resIsOK(res)
                        count ++
                        FirebaseController().send(sender, count.toString(), firebaseConfig.VERIFY_PATH)
                        if (count == 3) {
                            FirebaseController().send("ADMIN",
                                    "Cannot return to ${log.sender} at location ${log.senderLocation}",
                                    firebaseConfig.MESSAGE_PATH)
                            status.CALL_ADMIN
                        }
                        else status.VERIFY_RETURN_SENDER
                    }
                }
                else -> throw StatusNotFoundException(status = log.status)
            }
            log.status = current.status
            botPositionRepository.save(current)
            logRepository.save(log)
        } else throw StatusNotFoundException(status = current.status)
    }

    @RequestMapping(value = "/timeout", method = arrayOf(RequestMethod.PATCH))
    fun timeout(){
        //WaitForSender and WaitForTarget
        var current =  botPositionRepository.findByState("current")
        if (logRepository.findByStatus(current.status) != null){
            var log = logRepository.findByStatus(current.status)!!
            current.status = when(log.status){
                status.WAIT_FOR_SENDER -> {
                    status.FAILED
                }
                status.WAIT_FOR_TARGET -> {
                    // RETURN TO SENDER
                    val location = Location(current.position, log.senderLocation)
                    val res: String = restTemplate.postForObject(raspConfig.CALL, location, String::class.java)
                    resIsOK(res)
                    status.RETURNING
                }
                status.WAIT_FOR_RETURNING -> {
                    // CALL ADMIN //
                    FirebaseController().send("ADMIN",
                            "Cannot return to ${log.sender} at location ${log.senderLocation}",
                            firebaseConfig.MESSAGE_PATH)
                    status.CALL_ADMIN
                }
                else -> throw StatusNotFoundException(status = log.status)
            }
            log.status = current.status
            botPositionRepository.save(current)
            logRepository.save(log)
            if (current.status == status.FAILED){
                checkNextQueue(current, log)
            }
        }
        else throw StatusNotFoundException(status = current.status)
    }

    @RequestMapping(value = "/checkPoint", method = arrayOf(RequestMethod.GET))
    fun checkPoint(){
        var current =  botPositionRepository.findByState("current")
        val location = CurrentLocation(current.position)
        val res: String = restTemplate.postForObject(raspConfig.CHECK_POINT, location, String::class.java)
        resIsOK(res)
    }

    fun resIsOK(res: String){
        if (res != raspConfig.STATUS_OK) throw RaspStatusError(status = res)
    }

    fun checkNextQueue(current: BotPosition, log: Log){
        val waitList = logRepository.findByStatusOrderByCreatedAt(status.WAIT)
        current.status = if (waitList!!.isNotEmpty()){
            val nextQueue = waitList[0]
            nextQueue.status = status.CALLING
            FirebaseController().send(nextQueue.sender, "Robot is going to pick up from you.", firebaseConfig.MESSAGE_PATH)
            logRepository.save(nextQueue)
            val location = Location(current.position, log.senderLocation)
            val res: String = restTemplate.postForObject(raspConfig.CALL, location, String::class.java)
            resIsOK(res)
            status.CALLING
        } else {
            status.WAIT
        }
        botPositionRepository.save(current)
    }
}