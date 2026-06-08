package com.example.tts_like.data.repository

import com.example.tts_like.data.model.OrderStatus
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CommerceRepositoryTest {
    @Before
    fun setUp() {
        CommerceRepository.resetForTesting()
    }

    @After
    fun tearDown() {
        CommerceRepository.resetForTesting()
    }

    @Test
    fun addToCart_rejectsOutOfStockSku() {
        val product = requireNotNull(CommerceRepository.getProduct("prod_1"))
        val outOfStockSku = requireNotNull(product.skus.find { it.id == "sku_3" })

        val result = CommerceRepository.addToCart(product, outOfStockSku)

        assertFalse(result.success)
        assertTrue(result.message?.contains("库存不足") == true)
        assertTrue(CommerceRepository.cart.items.isEmpty())
    }

    @Test
    fun checkoutSelectedItems_createsPriceSnapshotAndRemovesPurchasedCartItem() {
        val product = requireNotNull(CommerceRepository.getProduct("prod_1"))
        val sku = requireNotNull(product.skus.find { it.id == "sku_1" })
        CommerceRepository.addToCart(product, sku)

        val checkout = CommerceRepository.checkoutSelectedItems()
        val order = CommerceRepository.createOrderFromPending()

        assertTrue(checkout.success)
        assertNotNull(order)
        assertEquals(OrderStatus.PENDING_PAYMENT, order?.status)
        assertEquals(99.0, order?.totalAmount ?: 0.0, 0.0)
        assertEquals(20.0, order?.discountAmount ?: 0.0, 0.0)
        assertEquals(79.0, order?.payAmount ?: 0.0, 0.0)
        assertTrue(CommerceRepository.cart.items.isEmpty())
        assertTrue(CommerceRepository.pendingCheckoutItems.isEmpty())
    }

    @Test
    fun payOrder_allowsSingleSuccessTransition() {
        val order = createPendingOrder()

        val firstPayment = CommerceRepository.payOrder(order.orderNo, success = true)
        val duplicatePayment = CommerceRepository.payOrder(order.orderNo, success = true)

        assertNotNull(firstPayment)
        assertEquals(OrderStatus.PAID, CommerceRepository.getOrderByNo(order.orderNo)?.status)
        assertEquals(null, duplicatePayment)
    }

    @Test
    fun expireOrder_disablesFurtherPayment() {
        val order = createPendingOrder()

        CommerceRepository.expireOrder(order.orderNo)

        val expiredOrder = requireNotNull(CommerceRepository.getOrderByNo(order.orderNo))
        assertEquals(OrderStatus.CANCELLED, expiredOrder.status)
        assertFalse(CommerceRepository.canPay(expiredOrder))
        assertEquals(null, CommerceRepository.payOrder(order.orderNo, success = true))
    }

    private fun createPendingOrder() = run {
        val product = requireNotNull(CommerceRepository.getProduct("prod_1"))
        val sku = requireNotNull(product.skus.find { it.id == "sku_1" })
        assertTrue(CommerceRepository.checkoutBuyNow(product, sku).success)
        requireNotNull(CommerceRepository.createOrderFromPending())
    }
}
