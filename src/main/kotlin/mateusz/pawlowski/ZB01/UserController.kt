package mateusz.pawlowski.ZB01

import com.fasterxml.jackson.annotation.JsonProperty
import mateusz.pawlowski.ZB01.Address.autoIncrement
import mateusz.pawlowski.ZB01.Address.nullable
import mateusz.pawlowski.ZB01.Address.references
import mateusz.pawlowski.ZB01.Address.uniqueIndex
import mateusz.pawlowski.ZB01.Data.Coordinates
import org.springframework.boot.autoconfigure.web.WebProperties
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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.util.*

private val logger = KotlinLogging.logger { }

inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}
object User: Table(){

    val id = varchar("id",10)
//    val addressId = (integer("address_id")references Address.id).nullable()
//    val coordinatesId = (integer("coordinates_id")references UserCoordinates.id).nullable()
//    val EmployId = (integer("employ_id")references UserEmploy.id).nullable()
//    val ccId = (integer("cc_id")references UserCreditCard.id).nullable()
//    val subscriptionId = (integer("subscription_id")references UserSubscription.id).nullable()
    val uid = varchar("uid",50)
    val password = varchar("password",50)
    val fname = varchar("first_name",50)
    val lname = varchar("last_name",50)
    val username = varchar("username",50)
    val email = varchar("email",50)
    val avatar = varchar("avatar",100)
    val gender = varchar("gender",20)
    val phonenumber = varchar("phone_number",50)
    val sin = varchar("social_insurance_number",30)
    val dob = varchar("date_of_birth", 50)

    override val primaryKey = PrimaryKey(id,name ="PK_id")
}
object Address: Table(){
    val addressId = (varchar("address_id",10)references User.id).nullable()
    //val id = varchar("id",10)
    val streetname = varchar("street_name",50)
    val streetaddrees = varchar("street_address",50)
    val zipcode = varchar("zip_code",50)
    val state = varchar("state",50)
    val country = varchar("country",50)

    override val primaryKey = PrimaryKey(addressId,name ="PK_id_address")
}
object UserCoordinates: Table(){

    val coordinatesId = (varchar("address_id",10) references User.id).nullable()
    val lat = varchar("lat",50)
    val lng = varchar("lng",50)

    override val primaryKey = PrimaryKey(coordinatesId,name ="PK_id_coordinates")
}
object UserEmploy: Table(){

    val employId = (varchar("address_id",10) references User.id).nullable()
    val title = varchar("title",50)
    val skill = varchar("key_skill",50)

    override val primaryKey = PrimaryKey(employId,name ="PK_id_employ")
}
object UserCreditCard: Table(){

    val ccId = (varchar("address_id",10) references User.id).nullable()
    val cc = varchar("cc_number",50)

    override val primaryKey = PrimaryKey(ccId,name ="PK_id_cc")
}
object UserSubscription: Table(){
    val subscriptionId = (varchar("address_id",10) references User.id).nullable()
    val plan = varchar("plan",50)
    val status = varchar("status",50)
    val pm = varchar("payment_method",50)
    val term = varchar("term",50)
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
        for ( i in 0..9) {
            val result: ResponseEntity<UserData>? = restTemplate.exchange(
                "https://random-data-api.com/api/v2/users",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<UserData>()
            )
            val c = result?.body?.uid
            println("Nobody lives in $c")
            if (result?.body?.uid == c) {
                Database.connect("jdbc:sqlite:/data/data.db", "org.sqlite.JDBC")
                transaction {
                    addLogger(StdOutSqlLogger)

                    SchemaUtils.create(User, Address, UserCoordinates, UserCreditCard, UserEmploy, UserSubscription)
//                    val existsOp = exists(User.select { User.email eq "${result?.body?.email}" })
//                    val res = Table.Dual.slice(existsOp).selectAll().first()
//                    val existsResult = res[existsOp]
//                    if (existsOp==null){
//                        println("nie ma powtorek")
//                    }

                    UserCoordinates.insert {

                        it[lat] = "${result?.body?.address?.coordinates?.lat}"
                        it[lng] = "${result?.body?.address?.coordinates?.lng}"
                    } get UserCoordinates.coordinatesId

                    UserSubscription.insert {

                        it[plan] = "${result?.body?.subscription?.plan}"
                        it[status] = "${result?.body?.subscription?.status}"
                        it[pm] = "${result?.body?.subscription?.paymentmethod}"
                        it[term] = "${result?.body?.subscription?.term}"
                    } get UserSubscription.subscriptionId

                    UserEmploy.insert {

                        it[title] = "${result?.body?.employment?.title}"
                        it[skill] = "${result?.body?.employment?.skill}"
                    } get UserEmploy.employId

                    UserCreditCard.insert {

                        it[cc] = "${result?.body?.creditCard?.ccnumber}"
                    } get UserCreditCard.ccId

                    Address.insert {
                        it[streetname] = "${result?.body?.address?.streetname}"
                        it[streetaddrees] = "${result?.body?.address?.streetaddress}"
                        it[country] = "${result?.body?.address?.country}"
                        it[state] = "${result?.body?.address?.state}"
                        it[zipcode] = "${result?.body?.address?.zipcode}"
                    } get Address.addressId

                    User.insert {
                        it[id] = "${result?.body?.id}"
                        it[uid] = "${result?.body?.uid}"
//                    it[addressId] =Address.id
//                    it[subscriptionId] =UserSubscription.id
//                    it[EmployId] =UserEmploy.id
//                    it[ccId] =UserCreditCard.id
//                    it[coordinatesId] =UserCoordinates.id
                        it[password] = "${result?.body?.password}"
                        it[fname] = "${result?.body?.firstname}"
                        it[lname] = "${result?.body?.lastname}"
                        it[username] = "${result?.body?.username}"
                        it[email] = "${result?.body?.email}"
                        it[avatar] = "${result?.body?.avatar}"
                        it[gender] = "${result?.body?.gender}"
                        it[phonenumber] = "${result?.body?.phonenumber}"
                        it[sin] = "${result?.body?.socialinsurancenumber}"
                        it[dob] = "${result?.body?.dateofbirth}"
                    }



                }


                result?.body?.let {
                    logger.info { "Resources found: " }
                    return ResponseEntity.ok(it)
                }
            }
        }
        return ResponseEntity.notFound().build()
    }
    @Scheduled(fixedRate = 60000)
    fun tenperson(){
        for(i in 0..9){
            person()
        }
    }
}
