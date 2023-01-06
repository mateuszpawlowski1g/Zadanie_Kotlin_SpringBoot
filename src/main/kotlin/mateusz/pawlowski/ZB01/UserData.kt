package mateusz.pawlowski.ZB01


import com.fasterxml.jackson.annotation.JsonProperty
import mateusz.pawlowski.ZB01.Data.*
import java.util.*

data class UserData(

    @JsonProperty("id")  val id: Long,
    @JsonProperty("uid")  val uid: String,
    val password: String,
    @JsonProperty("first_name") val firstname:String,
    @JsonProperty("last_name") val lastname: String,
    val username: String,
    val email: String,
    val avatar: String,
    val gender: String,
    @JsonProperty("phone_number") val phonenumber: String,
    @JsonProperty("social_insurance_number") val socialinsurancenumber: String,
    @JsonProperty("date_of_birth") val dateofbirth: String,
    @JsonProperty("employment") val employment: Employ,
    @JsonProperty("address") val address: address,
    @JsonProperty("credit_card") val creditCard: CreditCard,
    val subscription: Subscription
)

