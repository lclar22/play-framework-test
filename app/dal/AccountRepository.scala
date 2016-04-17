package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Account

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class AccountRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class AccountesTable(tag: Tag) extends Table[Account](tag, "account") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def code = column[String]("code")
    def name = column[String]("name")
    def type_1 = column[String]("type")
    def description = column[String]("description")
    def * = (id, code, name, type_1, description) <> ((Account.apply _).tupled, Account.unapply)
  }

  private val tableQ = TableQuery[AccountesTable]

  def create(code: String, name: String, type_1: String, description: String): Future[Account] = db.run {
    (tableQ.map(p => (p.code, p.name, p.type_1, p.description))
      returning tableQ.map(_.id)
      into ((nameAge, id) => Account(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4))
    ) += (code, name, type_1, description)
  }

  def list(): Future[Seq[Account]] = db.run {
    tableQ.result
  }

  // to cpy
  def getById(id: Long): Future[Seq[Account]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, code: String, name: String, type_1: String, description: String): Future[Seq[Account]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.code
    db.run(q.update(code))
    val q2 = for { c <- tableQ if c.id === id } yield c.name
    db.run(q2.update(name))
    val q3 = for { c <- tableQ if c.id === id } yield c.type_1
    db.run(q3.update(type_1))
    val q4 = for { c <- tableQ if c.id === id } yield c.description
    db.run(q4.update(description))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[Account]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    println("removed " + affectedRowsCount);
    tableQ.result
  }


  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.filter(_.id < 10L).map(s => (s.id, s.code)).result
  }

}
