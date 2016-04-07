package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.DiscountDetail
import models.RequestRow

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class DiscountDetailRepository @Inject() (dbConfigProvider: DatabaseConfigProvider,  repoRequestRow: RequestRowRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class DiscountDetailsTable(tag: Tag) extends Table[DiscountDetail](tag, "discountDetail") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def discountReport = column[Long]("discountReport")
    def requestRow = column[Long]("requestRow")
    def productorId = column[Long]("productorId")
    def status = column[String]("status")
    def amount = column[Int]("amount")
    def * = (id, discountReport, productorId, status, amount) <> ((DiscountDetail.apply _).tupled, DiscountDetail.unapply)
  }

  private val tableQ = TableQuery[DiscountDetailsTable]

  def create(discountReport: Long, productorId: Long, status: String, amount: Int): Future[DiscountDetail] = db.run {
    (tableQ.map(p => (p.discountReport, p.productorId, p.status, p.amount))
      returning tableQ.map(_.id)
      into ((nameAge, id) => DiscountDetail(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4))
    ) += (discountReport, productorId, status, amount)
  }

  def list(): Future[Seq[DiscountDetail]] = db.run {
    tableQ.result
  }

  def listByInsumo(id: Long): Future[Seq[DiscountDetail]] = db.run {
    tableQ.filter(_.discountReport === id).result
  }

  def listByReport(id: Long): Future[Seq[DiscountDetail]] = db.run {
    tableQ.filter(_.discountReport === id).result
  }

  // to cpy
  def getById(id: Long): Future[Seq[DiscountDetail]] = db.run {
    tableQ.filter(_.id === id).result
  }

  def generarReporte(requestRows: Seq[RequestRow], discountReportId: Long) = {
    requestRows.foreach { case (requestRow) => 
       val insertResult = db.run {
                  (tableQ.map(p => (p.discountReport, p.productorId, p.status, p.amount))
                    returning tableQ.map(_.id)
                    into ((nameAge, id) => DiscountDetail(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4))
                  ) += (discountReportId, requestRow.productorId, "borrador", requestRow.quantity)
                };
      
      insertResult.map(insertResultRow => repoRequestRow.updatePaid(1L, insertResultRow.amount).map(mm => println("DONE")))
      println("DONE");
    }
  }

  // update required to copy
  def update(id: Long, discountReport: Long, productorId: Long, status: String, amount: Int): Future[Seq[DiscountDetail]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.discountReport
    db.run(q.update(discountReport))
    val q2 = for { c <- tableQ if c.id === id } yield c.productorId
    db.run(q2.update(productorId))
    val q3 = for { c <- tableQ if c.id === id } yield c.status
    db.run(q3.update(status))
    val q4 = for { c <- tableQ if c.id === id } yield c.amount
    db.run(q4.update(amount))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[DiscountDetail]] = db.run {
    val q = tableQ.filter(_.id === id)
    val res = q.result
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action);
    affectedRowsCount.map(s=> println(s))
    res
  }
}
