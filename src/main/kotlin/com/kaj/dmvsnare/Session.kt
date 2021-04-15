package com.kaj.dmvsnare

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.cookies.*
import io.ktor.client.features.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.*
import io.ktor.http.content.TextContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock

class Session(val target: Target, logLevel: LogLevel = LogLevel.NONE) {
    private val client = HttpClient(CIO) {
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        install(HttpRedirect) {
            checkHttpMethod = false
        }
        defaultRequest {
            host = HOST
            headers {
                append(HttpHeaders.UserAgent, USER_AGENT)
            }
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = logLevel
        }
    }

    private suspend fun initSession() {
        client.get<HttpResponse>("/Webapp/_/_/_/en/SignIn/SessionEnd")
        client.post<HttpResponse>("/Webapp/_/_/_/en/WizardAppt/SelectAppointmentType") {
            body = TextContent(
                text = "apptType=${target.type.text}",
                contentType = ContentType.Application.FormUrlEncoded
            )
        }
        client.post<HttpResponse>("/Webapp/_/_/_/en/WizardAppt/SelectedUnit") {
            body = TextContent(
                text = "unitId=${target.location.id}",
                contentType = ContentType.Application.FormUrlEncoded
            )
        }
        println(sessionId() ?: "no cookies")
    }

    private suspend fun queryDate(): Map<String, String> {
        // /Webapp/_/_/_/en/WizardAppt/SlotsTime?date=05/18/2021&_=1618341252371
        val month = target.date.monthNumber.toString().padStart(2, '0')
        val day = target.date.dayOfMonth.toString().padStart(2, '0')
        val year = target.date.year
        val dateText = "$month/$day/$year"
        val epochText = (clock.now().epochSeconds - 20).toString() //let's scrape a cheeky 20 seconds off
        val res = client
            .get<HttpResponse>("/Webapp/_/_/_/en/WizardAppt/SlotsTime?date=$dateText&_=$epochText")
        val text = res.readText()
        return parseTimes(text)
    }

    private suspend fun submitTime(timeId: String) {
        ///Webapp/_/_/_/en/WizardAppt/DateAndTime
        //Date=05%2F20%2F2021&Time=900&Validate=Next

        //i wish kotlinx had a more robust datetime formatter like C#'s slib
        val month = target.date.monthNumber.toString().padStart(2, '0')
        val day = target.date.dayOfMonth.toString().padStart(2, '0')
        val year = target.date.year

        client.post<HttpResponse>("/Webapp/_/_/_/en/WizardAppt/DateAndTime") {
            body = TextContent(
                text = "Date=$month%2F$day%2F$year&Time=$timeId&Validate=Next",
                contentType = ContentType.Application.FormUrlEncoded
            )
        }
    }

    //the final dance.
    private suspend fun capture(): Boolean {
        //PersonalId=9&IsActive=on&Sex=Unknown&TelNumber1=PHONE&FirstName=NAME&LastName=LNAME&EMail=EMAIL
        // &Conf_EMail=EMAIL&2=on&1=on

        println("*****************************************************")
        println("*********** ATTEMPTING CAPTURE ALERT ALERT **********")
        println("*****************************************************")

        val response = client.post<HttpResponse>("/Webapp/_/_/_/en/WizardAppt/Customer") {
            body = TextContent(
                text = "PersonalId=9&IsActive=on&Sex=Unknown&TelNumber1=${User.phone}&" +
                        "FirstName=${User.firstName}&LastName=${User.lastName}" +
                        "&EMail=${User.email}&Conf_EMail=${User.email}&" +
                        "2=on&1=on",
                contentType = ContentType.Application.FormUrlEncoded
            )
        }

        val text = response.readText()
        return text.contains("This is to confirm your driver license appointment")
    }

    //default interval is 10 seconds
    suspend fun start(interval: Long = 10000, tolerance: Int = 10): Boolean {
        var sessionStartTime = clock.now()
        var result = false
        var faults = 0
        while (proceed && faults < tolerance) {
            try {
                val sessionId = sessionId()
                println("$target: looping with session id ${sessionId ?: "NULL"}")
                if (sessionStartTime.minutesUntilNow() > 20 || sessionId == null) {
                    println("$target: resetting session")
                    initSession()
                    sessionStartTime = clock.now()
                }
                val times = queryDate()
                if (!times.isNullOrEmpty()) {
                    submitTime(times.keys.last())  //set latest time
                    if (capture()) {
                        stop()
                        result = true
                    }
                }
                delay(interval)
            } catch (e: Exception) {
                println("Warning: ${e.message}")
                faults++
            } //print a warning, try again. if it keeps fucking up it will give up
        }
        println("$target: closing")
        client.close()
        return result
    }

    //stopping one session will stop them all
    private fun stop() {
        proceed = false
        println("$target: shutting down shop")
    }

    private fun cookie() = runBlocking {
        client.actualCookies(HOST).firstOrNull()
    }

    private fun sessionId() = cookie()?.value

    companion object {
        //quick and dirty way to handle coroutines
        private var proceed = true

        private val clock = Clock.System

        private fun parseTimes(json: String): Map<String, String> {
            val regex = "\"GroupStartTime\":(\\d\\d\\d),\"GroupStartTimeDisplay\":\"(\\d?\\d:\\d\\d \\w\\w)\"".toRegex()
            val result = regex.findAll(json).toList()
            if (result.isNotEmpty())
                println("********** found ${result.count()} available time(s) **********")
            else println("no available times")
            return result.associate { it.groupValues[1] to it.groupValues[2] }
        }
    }
}