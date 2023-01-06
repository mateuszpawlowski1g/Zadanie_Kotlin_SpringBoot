package mateusz.pawlowski.ZB01


import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId
import mateusz.pawlowski.ZB01.Address.coordinatesid
import mateusz.pawlowski.ZB01.Address.nullable
import mateusz.pawlowski.ZB01.Data.Coordinates
import mateusz.pawlowski.ZB01.User.nullable
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import mu.KotlinLogging
import org.jetbrains.annotations.NotNull
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.context.annotation.Primary
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.sqlite.SQLiteDataSource
import java.util.*


private val logger = KotlinLogging.logger { }

inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}
object User: Table(){
    val id = long("id").nullable()
    val uid = varchar("uid",50).nullable()
    val password = varchar("password",50).nullable()
    val fname = varchar("first_name",50).nullable()
    val lname = varchar("last_name",50).nullable()
    val username = varchar("username",50).nullable()
    val email = varchar("email",50).nullable()
    val avatar = varchar("avatar",100).nullable()
    val gender = varchar("gender",20).nullable()
    val phonenumber = varchar("phone_number",50).nullable()
    val sin = varchar("social_insurance_number",30).nullable()
    val dob = varchar("date_of_birth", 50).nullable()
    override val primaryKey = PrimaryKey(id,name ="PK_id")
}

object Address: Table(){
    val addressId = long("address_id").autoIncrement()

    val userid = long("user_id").nullable()
    val coordinatesid = (integer("coordinates_id")references UserCoordinates.coordinatesId).nullable()
    // val cityId = (integer("city_id") references Cities.id).nullable()
    val streetname = varchar("street_name",50).nullable()
    val streetaddrees = varchar("street_address",50).nullable()
    val zipcode = varchar("zip_code",50).nullable()
    val state = varchar("state",50).nullable()
    val country = varchar("country",50).nullable()

  override val primaryKey = PrimaryKey(addressId,name ="PK_id_address")
}
object UserCoordinates: Table(){

    val coordinatesId = integer("coordinates_id").autoIncrement()
    val userid = long("user_id").nullable()
    val lat = double("lat").nullable()
    val lng = double("lng").nullable()
    override val primaryKey = PrimaryKey(coordinatesId,name ="coordinates_id")
}
object UserEmploy: Table(){

    val employId = long("employ_id").autoIncrement()
    val userid = long("user_id").nullable()
    val title = varchar("title",50).nullable()
    val skill = varchar("key_skill",50).nullable()

    override val primaryKey = PrimaryKey(employId,name ="PK_id_employ")
}
object UserCreditCard: Table(){

    val ccId = long("credit_card_id").autoIncrement()
    val userid = long("user_id").nullable()
    val cc = varchar("cc_number",50).nullable()

    override val primaryKey = PrimaryKey(ccId,name ="PK_id_cc")
}
object UserSubscription: Table(){
    val subscriptionId = long("subscription_id").autoIncrement()
    val userid = long("user_id").nullable()
    val plan = varchar("plan",50).nullable()
    val status = varchar("status",50).nullable()
    val pm = varchar("payment_method",50).nullable()
    val term = varchar("term",50).nullable()
    override val primaryKey = PrimaryKey(subscriptionId,name ="PK_id_subscription")
}


@RestController
@EnableScheduling

class UserController(val restTemplate: RestTemplate){
    @GetMapping("/person")
    fun person(): ResponseEntity<UserData> {
        logger.info { "GET person" }

        val headers = HttpHeaders()
        headers.set("x-org-id", "pwf.no")
        headers.set("x-client", "kotlin-test-client")

            val result: ResponseEntity<UserData>? = restTemplate.exchange(
                "https://random-data-api.com/api/v2/users",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<UserData>()
            )

        Database.connect("jdbc:sqlite:/data/data.db", "org.sqlite.JDBC")

        transaction {
            addLogger(StdOutSqlLogger)
            val query = User.selectAll()
            SchemaUtils.create(User, Address, UserCoordinates, UserCreditCard, UserEmploy, UserSubscription)
            val currentEmail= query.forEach { it[User.email] == result?.body?.email }

            if (currentEmail.equals( result?.body?.email!!)) {

                User.update({ User.email like result?.body?.email!! }) {
                    it[id] = result?.body?.id
                    it[uid] = result?.body?.uid
                    it[password] = result?.body?.password
                    it[fname] = result?.body?.firstname
                    it[lname] = result?.body?.lastname
                    it[username] = result?.body?.username
                    it[email] = result?.body?.email
                    it[avatar] = result?.body?.avatar
                    it[gender] =result?.body?.gender
                    it[phonenumber] = result?.body?.phonenumber
                    it[sin] = result?.body?.socialinsurancenumber
                    it[dob] = result?.body?.dateofbirth
                }
            } else {



//                UserCoordinates.insert {
//                    it[userid] = result?.body?.id
//                    it[lat] =  result?.body?.address?.coordinates?.lat
//                    it[lng] = result?.body?.address?.coordinates?.lng
//                } get UserCoordinates.coordinatesId
//                //
                val saintPetersburgId = UserCoordinates.insert {
                    it[userid] = result?.body?.id
                    it[lat] =  result?.body?.address?.coordinates?.lat
                    it[lng] = result?.body?.address?.coordinates?.lng
                } get UserCoordinates.coordinatesId
//
                UserSubscription.insert {
                    it[userid] = result?.body?.id
                    it[plan] = result?.body?.subscription?.plan
                    it[status] =  result?.body?.subscription?.status
                    it[pm] = result?.body?.subscription?.paymentmethod
                    it[term] = result?.body?.subscription?.term
                }

                UserEmploy.insert {
                    it[userid] = result?.body?.id
                    it[title] = result?.body?.employment?.title
                    it[skill] = result?.body?.employment?.skill
                }

                UserCreditCard.insert {
                    it[userid] = result?.body?.id
                    it[cc] = result?.body?.creditCard?.ccnumber
                }

                Address.insert {
                    it[userid] = result?.body?.id
                    it[streetname] = result?.body?.address?.streetname
                    it[Address.coordinatesid] =  saintPetersburgId
                    it[streetaddrees] = result?.body?.address?.streetaddress
                    it[country] = result?.body?.address?.country
                    it[state] = result?.body?.address?.state
                    it[zipcode] = result?.body?.address?.zipcode
                }

                User.insert {
                    it[id] = result?.body?.id
                    it[uid] = result?.body?.uid
                    it[password] = result?.body?.password
                    it[fname] = result?.body?.firstname
                    it[lname] = result?.body?.lastname
                    it[username] = result?.body?.username
                    it[email] = result?.body?.email
                    it[avatar] = result?.body?.avatar
                    it[gender] =result?.body?.gender
                    it[phonenumber] = result?.body?.phonenumber
                    it[sin] = result?.body?.socialinsurancenumber
                    it[dob] = result?.body?.dateofbirth
                }


            }
        }

            result?.body?.let {
                logger.info { "Resources found: " }
                return ResponseEntity.ok(it)
            }



        return ResponseEntity.notFound().build()
    }
    @Scheduled(fixedRate = 600000)
    fun tenperson(){
        for(i in 0..9){
            person()
        }
    }


}


