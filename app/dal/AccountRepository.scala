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
    def negativo = column[String]("negativo")
    def parent = column[Long]("parent")
    def description = column[String]("description")
    def child = column[Boolean]("child")
    def * = (id, code, name, type_1, negativo, parent, description, child) <> ((Account.apply _).tupled, Account.unapply)
  }

  private val tableQ = TableQuery[AccountesTable]

  def create(code: String, name: String, type_1: String, negativo: String, parent: Long, description: String): Future[Account] = db.run {
    (tableQ.map(p => (p.code, p.name, p.type_1, p.negativo, p.parent, p.description, p.child))
      returning tableQ.map(_.id)
      into ((nameAge, id) => Account(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5, nameAge._6, nameAge._7))
    ) += (code, name, type_1, negativo, parent, description, true)
  }

  def list(): Future[Seq[Account]] = db.run {
    tableQ.sortBy(m => (m.code)).result
  }

  // to cpy
  def getById(id: Long): Future[Seq[Account]] = db.run {
    tableQ.filter(_.id === id).result
  }

  def getByParent(id: Long): Future[Seq[Account]] = db.run {
    tableQ.sortBy(m => (m.code)).filter(_.parent === id).result
  }

  // to cpy
  def getByPasivo(id: Long): Future[Seq[Account]] = db.run {
    tableQ.sortBy(m => (m.code)).filter(_.id === id).result
  }

  // to cpy
  def getByActivo(id: Long): Future[Seq[Account]] = db.run {
    tableQ.sortBy(m => (m.code)).filter(_.id === id).result
  }

// to cpy
  def getByPatrimonio(id: Long): Future[Seq[Account]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, code: String, name: String, type_1: String, negativo: String, parent: Long, description: String): Future[Seq[Account]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.code
    db.run(q.update(code))
    val q2 = for { c <- tableQ if c.id === id } yield c.name
    db.run(q2.update(name))
    val q3 = for { c <- tableQ if c.id === id } yield c.type_1
    db.run(q3.update(type_1))
    val q4 = for { c <- tableQ if c.id === id } yield c.negativo
    db.run(q4.update(negativo))
    val q5 = for { c <- tableQ if c.id === id } yield c.parent
    db.run(q5.update(parent))
    val q6 = for { c <- tableQ if c.id === id } yield c.description
    db.run(q6.update(description))
    tableQ.filter(_.id === id).result
  }

  // Update it when generate the report
  def updateParentFlag(id: Long): Future[Seq[Account]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.child
    db.run(q.update(true))
    tableQ.filter(_.id === id).result
  }

  def delete(id: Long): Future[Seq[Account]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    println("removed " + affectedRowsCount);
    tableQ.result
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.sortBy(m => (m.code)).map(s => (s.id, s.name)).result
  }

  def getListObjs(): Future[Seq[Account]] = db.run {
    tableQ.sortBy(m => (m.code)).sortBy(m => (m.code)).result
  }

}
