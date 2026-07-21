package com.cocido.mipelu.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val shortDateFormat = SimpleDateFormat("d MMM yyyy", Locale.Builder().setLanguage("es").setRegion("ES").build())

fun Long.toShortDateEs(): String = shortDateFormat.format(Date(this))
