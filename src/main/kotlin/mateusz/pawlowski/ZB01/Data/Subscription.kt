package mateusz.pawlowski.ZB01.Data

import com.fasterxml.jackson.annotation.JsonProperty

data class Subscription (
    val plan: String,
    val status: String,
    @JsonProperty("payment_method") val paymentmethod:String,
    val term: String
)