package mateusz.pawlowski.ZB01.Data

import com.fasterxml.jackson.annotation.JsonProperty

data class Employ(

    @JsonProperty("title") val title: String?,
    @JsonProperty("key_skill") val skill: String
)