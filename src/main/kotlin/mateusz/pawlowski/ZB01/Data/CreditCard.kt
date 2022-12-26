package mateusz.pawlowski.ZB01.Data

import com.fasterxml.jackson.annotation.JsonProperty

data class CreditCard(
    @JsonProperty("cc_number") val ccnumber: String
)