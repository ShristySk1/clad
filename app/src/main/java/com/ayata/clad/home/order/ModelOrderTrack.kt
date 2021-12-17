package com.ayata.clad.home.order

class ModelOrderTrack(
    var orderTrackTitle: String,
    var orderTrackDescription: String,
    var ordertype: String,
    var color: Int,
    var actual: Boolean
) {

    companion object {
        const val ORDER_TYPE_PLACED = "Order Placed"
        const val ORDER_TYPE_CONFIRMED = "Order Confirmed"
        const val ORDER_TYPE_PROCESS = "Order Processed"
        const val ORDER_TYPE_SHIP = "Ready to Ship"
        const val ORDER_TYPE_DELIVERY = "Out For Delivery"
        const val ORDER_TYPE_DELIVERED = "Delivered"
        const val ORDER_TYPE_NONE = "None"
    }
}