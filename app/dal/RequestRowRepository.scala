package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.RequestRow

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class RequestRowRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class RequestRowTable(tag: Tag) extends Table[RequestRow](tag, "requestRow") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def requestId = column[Long]("requestId")
    def productId = column[Long]("productId")
    def productorId = column[Long]("productorId")
    def quantity = column[Int]("quantity")
    def status = column[String]("status")
    def * = (id, requestId, productId, productorId, quantity, status) <> ((RequestRow.apply _).tupled, RequestRow.unapply)
  }

  private val tableQ = TableQuery[RequestRowTable]

  def create(requestId: Long, productId: Long, productorId: Long, quantity: Int, status: String): Future[RequestRow] = db.run {
    (tableQ.map(p => (p.requestId, p.productId, p.productorId, p.quantity, p.status))
      returning tableQ.map(_.id)
      into ((nameAge, id) => RequestRow(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5))
    ) += (requestId, productId, productorId, quantity, status)
  }

  def list(): Future[Seq[RequestRow]] = db.run {
    tableQ.result
  }

    // to cpy
  def getById(id: Long): Future[Seq[RequestRow]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, requestId: Long, productId: Long, productorId: Long, quantity: Int, status: String): Future[Seq[RequestRow]] = db.run {
    val q2 = for { c <- tableQ if c.id === id } yield c.requestId
    db.run(q2.update(requestId))
    val q = for { c <- tableQ if c.id === id } yield c.productId
    db.run(q.update(productId))
    val q3 = for { c <- tableQ if c.id === id } yield c.productorId
    db.run(q3.update(productorId))
    val q4 = for { c <- tableQ if c.id === id } yield c.quantity
    db.run(q4.update(quantity))
    val q5 = for { c <- tableQ if c.id === id } yield c.status
    db.run(q5.update(status))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[RequestRow]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    println("removed " + affectedRowsCount);
    tableQ.result
  }
}