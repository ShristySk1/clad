package com.ayata.clad.profile.myorder.order

class ModelOrderTrack(
    var orderTrackTitle: String,
    var orderTrackDescription: String,
    var ordertype: String,
    var color: Int,
    var actual: Boolean
) {

    companion object {
        const val ORDER_TYPE_PLACED = "Order Placed"
        const val ORDER_TYPE_DISPATCHED = "Order Dispatched"
        const val ORDER_TYPE_TRANSIT = "Order in Transit"
        const val ORDER_TYPE_DELIVERED = "Delivered Successfully"
        const val ORDER_TYPE_NONE = "None"
    }
}