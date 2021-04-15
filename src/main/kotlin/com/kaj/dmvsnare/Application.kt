package com.kaj.dmvsnare

import kotlinx.datetime.*

/*
    All the necessary config should be in this file
        Targets should be self explanatory
        User object should be self explanatory

    if you need to add more locations you can go to the dmv site and
    if you inspect the location you need there should be a value associated.
    put this info into the Location enum class in the Snare.kt file
 */

fun main() {
    val targets = listOf(
        Target(Location.WRALEIGH, LocalDate(2020, Month.APRIL, 15), AppointmentType.KNOWLEDGE),
        Target(Location.GARNER, LocalDate(2021, Month.APRIL, 15), AppointmentType.KNOWLEDGE),
        Target(Location.NRALEIGH, LocalDate(2020, Month.APRIL, 15), AppointmentType.KNOWLEDGE),
        Target(Location.CARY, LocalDate(2021, Month.APRIL, 15), AppointmentType.KNOWLEDGE),
        Target(Location.WRALEIGH, LocalDate(2021, Month.APRIL, 16), AppointmentType.KNOWLEDGE),
        Target(Location.GARNER, LocalDate(2021, Month.APRIL, 16), AppointmentType.KNOWLEDGE),
        Target(Location.NRALEIGH, LocalDate(2021, Month.APRIL, 16), AppointmentType.KNOWLEDGE),
        Target(Location.CARY, LocalDate(2021, Month.APRIL, 16), AppointmentType.KNOWLEDGE),
        Target(Location.WRALEIGH, LocalDate(2021, Month.APRIL, 19), AppointmentType.KNOWLEDGE),
        Target(Location.GARNER, LocalDate(2021, Month.APRIL, 19), AppointmentType.KNOWLEDGE),
        Target(Location.NRALEIGH, LocalDate(2021, Month.APRIL, 19), AppointmentType.KNOWLEDGE),
        Target(Location.CARY, LocalDate(2021, Month.APRIL, 19), AppointmentType.KNOWLEDGE),
        Target(Location.WRALEIGH, LocalDate(2021, Month.APRIL, 20), AppointmentType.KNOWLEDGE),
        Target(Location.GARNER, LocalDate(2021, Month.APRIL, 20), AppointmentType.KNOWLEDGE),
        Target(Location.NRALEIGH, LocalDate(2021, Month.APRIL, 20), AppointmentType.KNOWLEDGE),
        Target(Location.CARY, LocalDate(2021, Month.APRIL, 20), AppointmentType.KNOWLEDGE),
    )
    val snare = Snare(targets)
    snare.ensnare()
    println("If everything went to plan you should be " +
            "getting an email or text about your appointment soon")
}

object User {
    const val firstName = "John"
    const val lastName = "Doe"
    const val email = "example%40example.com" //keep the @ as %40
    const val phone = "5551234567"
}