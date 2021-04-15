package com.kaj.dmvsnare

import kotlinx.coroutines.*
import kotlinx.datetime.*

const val HOST = "skiptheline.ncdot.gov"
const val USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36"

class Target(
    val location: Location,
    val date: LocalDate,
    val type: AppointmentType = AppointmentType.RENEWAL
) {
    override fun toString() = "${location.name} - $date"
}

//if what you need isn't here you'll have to add it yourself
enum class Location(val id: Int) {
    WRALEIGH(9),
    GARNER(69), //nice
    NRALEIGH(10),
    CARY(66)
}

//I've never tested this thoroughly, look here first for issues.
//if you need more appointment types inspect the dmv site,
//but remember to replace whitespace and other special chars.
//I only ever used KNOWLEDGE so I don't know first hand
//that the other strings work
enum class AppointmentType(val text: String) {
    KNOWLEDGE("Knowledge+%2F+Computer+Test"),
    RENEWAL("Driver+License+Renewal"),
    DUPLICATE("Driver+License+Duplicate"),
}


class Snare(private val targets: List<Target>) {
    fun ensnare() = runBlocking {
        val sessions = targets.map { Session(it) }
        sessions.forEach {
            launch {
                if (it.start())
                    println("you seem to have captured an appointment at ${it.target}")
            }
        }
    }
}
