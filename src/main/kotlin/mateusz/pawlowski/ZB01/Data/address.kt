package mateusz.pawlowski.ZB01.Data

import com.fasterxml.jackson.annotation.JsonProperty

data class address (

    val city: String,
    @JsonProperty("street_name") val streetname: String,
    @JsonProperty("street_address") val streetaddress: String,
    @JsonProperty("zip_code") val zipcode: String,
    val state: String,
    val country: String,
    val coordinates: Coordinates
)
