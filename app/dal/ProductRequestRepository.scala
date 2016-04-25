package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.ProductRequest

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class ProductRequestRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class ProductRequestTable(tag: Tag) extends Table[ProductRequest](tag, "productRequest") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def date = column[String]("date")
    def veterinario = column[Long]("veterinario")
    def storekeeper = column[Long]("storekeeper")
    def status = column[String]("status")
    def detail = column[String]("detail")
    def type_1 = column[String]("type")
    def * = (id, date, veterinario, storekeeper, status, detail, type_1) <> ((ProductRequest.apply _).tupled, ProductRequest.unapply)
  }

  private val tableQ = TableQuery[ProductRequestTable]

  def create(date: String, veterinario: Long, storekeeper: Long, status: String, detail: String, type_1: String): Future[ProductRequest] = db.run {
    (tableQ.map(p => (p.date, p.veterinario, p.storekeeper, p.status, p.detail, p.type_1))
      returning tableQ.map(_.id)
      into ((nameAge, id) => ProductRequest(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5, nameAge._6))
    ) += (date, veterinario, storekeeper, status, detail, type_1)
  }

  def list(): Future[Seq[ProductRequest]] = db.run {
    tableQ.result
  }

  def listByVeterinario(id: Long): Future[Seq[ProductRequest]] = db.run {
    tableQ.filter(_.veterinario === id).result
  }

  def listByStorekeeper(id: Long): Future[Seq[ProductRequest]] = db.run {
    tableQ.filter(_.storekeeper === id).result
  }

  def listByInsumoUser(id: Long): Future[Seq[ProductRequest]] = db.run {
    tableQ.filter(_.veterinario === id).result
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.filter(_.id < 10L).map(s => (s.id, s.date)).result
  }

    // to cpy
  def getById(id: Long): Future[Seq[ProductRequest]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, date: String, veterinario: Long, storekeeper: Long, status: String, detail: String, type_1: String): Future[Seq[ProductRequest]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.date
    db.run(q.update(date))
    val q2 = for { c <- tableQ if c.id === id } yield c.veterinario
    db.run(q2.update(veterinario))
    val q3 = for { c <- tableQ if c.id === id } yield c.storekeeper
    db.run(q3.update(storekeeper))
    val q4 = for { c <- tableQ if c.id === id } yield c.status
    db.run(q4.update(status))
    val q5 = for { c <- tableQ if c.id === id } yield c.detail
    db.run(q5.update(detail))
    val q6 = for { c <- tableQ if c.id === id } yield c.type_1
    db.run(q6.update(type_1))
    tableQ.filter(_.id === id).result
  }

  // Update the status to enviado status
  def sendById(id: Long): Future[Seq[ProductRequest]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.status
    db.run(q.update("enviado"))
    tableQ.filter(_.id === id).result
  }

  // Update the status to enviado status
  def acceptById(id: Long): Future[Seq[ProductRequest]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.status
    db.run(q.update("aceptado"))
    tableQ.filter(_.id === id).result
  }

  // Update the status to finalizado status
  def finishById(id: Long): Future[Seq[ProductRequest]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.status
    db.run(q.update("finalizado"))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[ProductRequest]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    println("removed " + affectedRowsCount);
    tableQ.result
  }
}
