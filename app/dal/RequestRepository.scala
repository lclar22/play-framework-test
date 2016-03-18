package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.RequestModel

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class RequestRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class RequestTable(tag: Tag) extends Table[RequestModel](tag, "request") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def date = column[String]("date")
    def veterinario = column[Long]("veterinario")
    def storekeeper = column[Long]("storekeeper")
    def status = column[String]("status")
    def detail = column[String]("detail")
    def * = (id, date, veterinario, storekeeper, status, detail) <> ((RequestModel.apply _).tupled, RequestModel.unapply)
  }

  private val tableQ = TableQuery[RequestTable]

  def create(date: String, veterinario: Long, storekeeper: Long, status: String, detail: String): Future[RequestModel] = db.run {
    (tableQ.map(p => (p.date, p.veterinario, p.storekeeper, p.status, p.detail))
      returning tableQ.map(_.id)
      into ((nameAge, id) => RequestModel(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5))
    ) += (date, veterinario, storekeeper, status, detail)
  }

  def list(): Future[Seq[RequestModel]] = db.run {
    tableQ.result
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.filter(_.id < 10L).map(s => (s.id, s.date)).result
  }

    // to cpy
  def getById(id: Long): Future[Seq[RequestModel]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, date: String, veterinario: Long, storekeeper: Long, status: String, detail: String): Future[Seq[RequestModel]] = db.run {
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
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[RequestModel]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    println("removed " + affectedRowsCount);
    tableQ.result
  }
}
