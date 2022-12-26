package mateusz.pawlowski.ZB01

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.hateoas.config.EnableHypermediaSupport


@EnableHypermediaSupport(type = [(EnableHypermediaSupport.HypermediaType.HAL)])
@SpringBootApplication
class Zb01Application

fun main(args: Array<String>) {
	SpringApplication.run(Zb01Application::class.java,*args)
//	Database.connect("jdbc:sqlite:/data/data.db", "org.sqlite.JDBC")
//	transaction {
//		addLogger(StdOutSqlLogger)
//
//		SchemaUtils.create (User)
//
//		val xd = User.insert {
//			it[id] = "132452"
//		} get User.id
//		User.insert {
//			it[id] = "1"
//			it[name] = "Andrey"
//			it[User.uid] = xd
//		}
//
//		SchemaUtils.drop (User)
//	}

}
