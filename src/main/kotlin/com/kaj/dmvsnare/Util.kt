package com.kaj.dmvsnare

import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.cookies
import io.ktor.http.URLBuilder
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@OptIn(ExperimentalTime::class)
fun Int.toMinutes() = toDuration(DurationUnit.MINUTES)

fun Instant.minutesUntil(other: Instant) = until(other, DateTimeUnit.MINUTE)

fun Instant.minutesUntilNow() = minutesUntil(Clock.System.now())

suspend fun HttpClient.actualCookies(url: String) =
    cookies(URLBuilder(host = url).build())