package `in`.coupsome.model

enum class PaymentMode( val value: String) {
    ADDED("0"),
    VERIFIED("1"),
    REDEEMED("2"),
    REJECTED("3");


    companion object {
        fun fromInt(value: Int) = values().first { it.ordinal == value }
    }
}