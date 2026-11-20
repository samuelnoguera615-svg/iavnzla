package com.veneca.app.data

import com.veneca.app.model.LiveExchangeRates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExchangeRateService {

    suspend fun fetchLiveRates(): LiveExchangeRates = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL("https://ve.dolarapi.com/v1/dolares")
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "iavnzla-android/1.0")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val jsonArray = JSONArray(response)
                var bcv = 918.77
                var usdt = 1066.78

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val fuente = item.optString("fuente", "").lowercase()
                    val nombre = item.optString("nombre", "").lowercase()
                    val promedio = item.optDouble("promedio", 0.0)

                    if (promedio > 0.0) {
                        if (fuente == "oficial" || nombre.contains("dólar") || nombre.contains("bcv")) {
                            bcv = promedio
                        } else if (fuente == "paralelo" || nombre.contains("paralelo") || nombre.contains("usdt")) {
                            usdt = promedio
                        }
                    }
                }

                val now = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                LiveExchangeRates(
                    bcvRate = bcv,
                    usdtRate = usdt,
                    lastUpdated = now,
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                LiveExchangeRates(
                    bcvRate = 918.77,
                    usdtRate = 1066.78,
                    lastUpdated = "Referencial",
                    isLoading = false,
                    errorMessage = "HTTP ${connection.responseCode}"
                )
            }
        } catch (e: Exception) {
            LiveExchangeRates(
                bcvRate = 918.77,
                usdtRate = 1066.78,
                lastUpdated = "Offline",
                isLoading = false,
                errorMessage = e.message
            )
        } finally {
            connection?.disconnect()
        }
    }
}
