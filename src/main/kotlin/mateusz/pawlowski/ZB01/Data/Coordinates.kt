package mateusz.pawlowski.ZB01.Data

import com.fasterxml.jackson.annotation.JsonProperty

data class Coordinates (

    @JsonProperty("lat") val lat: Double,
    @JsonProperty("lng") val lng: Double
)
