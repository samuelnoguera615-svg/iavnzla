package com.veneca.app.data

import android.content.Context
import com.veneca.app.model.OrderStatus
import com.veneca.app.model.P2POrder
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class OrderRepository(context: Context) {
    private val preferences = context.getSharedPreferences("iavnzla_orders_clean", Context.MODE_PRIVATE)

    fun getAllOrders(): List<P2POrder> {
        val raw = preferences.getString(KEY_ORDERS, null) ?: return emptyList()
        val array = JSONArray(raw)
        return (0 until array.length()).map { idx ->
            val obj = array.getJSONObject(idx)
            P2POrder(
                id = obj.getString("id"),
                productId = obj.getString("productId"),
                productName = obj.getString("productName"),
                buyerId = obj.getString("buyerId"),
                buyerName = obj.getString("buyerName"),
                sellerId = obj.getString("sellerId"),
                sellerName = obj.getString("sellerName"),
                quantity = obj.getInt("quantity"),
                unitPriceUsd = obj.getDouble("unitPriceUsd"),
                totalUsd = obj.getDouble("totalUsd"),
                totalVes = obj.getDouble("totalVes"),
                isWholesale = obj.getBoolean("isWholesale"),
                paymentMethod = obj.getString("paymentMethod"),
                sellerPaymentDetails = obj.getString("sellerPaymentDetails"),
                buyerPaymentRef = obj.optString("buyerPaymentRef", "").ifBlank { null },
                sellerPaymentRef = obj.optString("sellerPaymentRef", "").ifBlank { null },
                status = OrderStatus.valueOf(obj.getString("status")),
                createdAt = obj.getLong("createdAt"),
                completedAt = if (obj.isNull("completedAt")) null else obj.getLong("completedAt"),
                isIaCuotasOrder = obj.optBoolean("isIaCuotasOrder", false),
                iaDownPaymentUsd = obj.optDouble("iaDownPaymentUsd", 0.0),
                iaInstallmentAmountUsd = obj.optDouble("iaInstallmentAmountUsd", 0.0),
                iaTotalInstallments = obj.optInt("iaTotalInstallments", 3),
                iaInstallmentsPaid = obj.optInt("iaInstallmentsPaid", 1)
            )
        }
    }

    fun saveOrders(orders: List<P2POrder>) {
        val array = JSONArray()
        orders.forEach { o ->
            array.put(JSONObject().apply {
                put("id", o.id)
                put("productId", o.productId)
                put("productName", o.productName)
                put("buyerId", o.buyerId)
                put("buyerName", o.buyerName)
                put("sellerId", o.sellerId)
                put("sellerName", o.sellerName)
                put("quantity", o.quantity)
                put("unitPriceUsd", o.unitPriceUsd)
                put("totalUsd", o.totalUsd)
                put("totalVes", o.totalVes)
                put("isWholesale", o.isWholesale)
                put("paymentMethod", o.paymentMethod)
                put("sellerPaymentDetails", o.sellerPaymentDetails)
                put("buyerPaymentRef", o.buyerPaymentRef ?: "")
                put("sellerPaymentRef", o.sellerPaymentRef ?: "")
                put("status", o.status.name)
                put("createdAt", o.createdAt)
                if (o.completedAt != null) put("completedAt", o.completedAt)
                put("isIaCuotasOrder", o.isIaCuotasOrder)
                put("iaDownPaymentUsd", o.iaDownPaymentUsd)
                put("iaInstallmentAmountUsd", o.iaInstallmentAmountUsd)
                put("iaTotalInstallments", o.iaTotalInstallments)
                put("iaInstallmentsPaid", o.iaInstallmentsPaid)
            })
        }
        preferences.edit().putString(KEY_ORDERS, array.toString()).apply()
    }

    fun createOrder(
        productId: String,
        productName: String,
        buyerId: String,
        buyerName: String,
        sellerId: String,
        sellerName: String,
        quantity: Int,
        unitPriceUsd: Double,
        totalUsd: Double,
        totalVes: Double,
        isWholesale: Boolean,
        paymentMethod: String,
        sellerPaymentDetails: String,
        isIaCuotasOrder: Boolean = false,
        iaDownPaymentUsd: Double = 0.0,
        iaInstallmentAmountUsd: Double = 0.0
    ): P2POrder {
        val prefix = if (isIaCuotasOrder) "IAC-" else "IAV-"
        val newOrder = P2POrder(
            id = "$prefix${Random.nextInt(10000, 99999)}",
            productId = productId,
            productName = productName,
            buyerId = buyerId,
            buyerName = buyerName,
            sellerId = sellerId,
            sellerName = sellerName,
            quantity = quantity,
            unitPriceUsd = unitPriceUsd,
            totalUsd = totalUsd,
            totalVes = totalVes,
            isWholesale = isWholesale,
            paymentMethod = paymentMethod,
            sellerPaymentDetails = sellerPaymentDetails,
            status = OrderStatus.PENDING_PAYMENT,
            isIaCuotasOrder = isIaCuotasOrder,
            iaDownPaymentUsd = iaDownPaymentUsd,
            iaInstallmentAmountUsd = iaInstallmentAmountUsd,
            iaTotalInstallments = 3,
            iaInstallmentsPaid = 0
        )
        val current = getAllOrders().toMutableList()
        current.add(0, newOrder)
        saveOrders(current)
        return newOrder
    }

    fun buyerSubmitPaymentRef(orderId: String, paymentRef: String): Result<P2POrder> {
        val trimmed = paymentRef.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("Debes ingresar el número de referencia o ID de comprobante"))
        }
        val current = getAllOrders().toMutableList()
        val index = current.indexOfFirst { it.id == orderId }
        if (index == -1) return Result.failure(IllegalArgumentException("Orden no encontrada"))

        val existing = current[index]
        if (existing.status != OrderStatus.PENDING_PAYMENT) {
            return Result.failure(IllegalStateException("La orden no está en espera de pago"))
        }

        val updated = existing.copy(
            buyerPaymentRef = trimmed,
            status = OrderStatus.PAID_PENDING_SELLER,
            iaInstallmentsPaid = if (existing.isIaCuotasOrder) 1 else 0
        )
        current[index] = updated
        saveOrders(current)
        return Result.success(updated)
    }

    fun sellerConfirmPayment(orderId: String, verificationRef: String): Result<P2POrder> {
        val trimmed = verificationRef.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("El vendedor debe ingresar el ID de confirmación bancaria"))
        }
        val current = getAllOrders().toMutableList()
        val index = current.indexOfFirst { it.id == orderId }
        if (index == -1) return Result.failure(IllegalArgumentException("Orden no encontrada"))

        val existing = current[index]
        if (existing.status != OrderStatus.PAID_PENDING_SELLER) {
            return Result.failure(IllegalStateException("La orden no está pendiente de verificación"))
        }

        val updated = existing.copy(
            sellerPaymentRef = trimmed,
            status = OrderStatus.COMPLETED,
            completedAt = System.currentTimeMillis()
        )
        current[index] = updated
        saveOrders(current)
        return Result.success(updated)
    }

    fun cancelOrder(orderId: String): Result<Unit> {
        val current = getAllOrders().toMutableList()
        val index = current.indexOfFirst { it.id == orderId }
        if (index == -1) return Result.failure(IllegalArgumentException("Orden no encontrada"))

        val existing = current[index]
        if (existing.status == OrderStatus.COMPLETED) {
            return Result.failure(IllegalStateException("No puedes cancelar una orden completada"))
        }

        current[index] = existing.copy(status = OrderStatus.CANCELLED)
        saveOrders(current)
        return Result.success(Unit)
    }

    fun clearAllOrders() {
        saveOrders(emptyList())
    }

    companion object {
        private const val KEY_ORDERS = "iavnzla_orders_clean"
    }
}
