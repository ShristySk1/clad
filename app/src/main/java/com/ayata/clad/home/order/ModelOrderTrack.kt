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
        const val ORDER_TYPE_DISPATCHED = "Out for dispatched"
        const val ORDER_TYPE_TRANSIT = "Out For transit"
        const val ORDER_TYPE_DELIVERED = "Delivered"
        const val ORDER_TYPE_NONE = "None"
    }
}