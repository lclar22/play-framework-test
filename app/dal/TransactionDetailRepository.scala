package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.TransactionDetail

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class TransactionDetailRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class TransactionDetailsTable(tag: Tag) extends Table[TransactionDetail](tag, "transactionDetail") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def transactionId = column[Long]("transaction")
    def accountId = column[Long]("account")
    def debit = column[Double]("debit")
    def credit = column[Double]("credit")
    def * = (id, transactionId, accountId, debit, credit) <> ((TransactionDetail.apply _).tupled, TransactionDetail.unapply)
  }

  private val tableQ = TableQuery[TransactionDetailsTable]

  def create(transactionId: Long, accountId: Long, debit: Double, credit: Double): Future[TransactionDetail] = db.run {
    (tableQ.map(p => (p.transactionId, p.accountId, p.debit, p.credit))
      returning tableQ.map(_.id)
      into ((nameAge, id) => TransactionDetail(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4))
    ) += (transactionId, accountId, debit, credit)
  }

  def list(): Future[Seq[TransactionDetail]] = db.run {
    tableQ.result
  }

  def listByTransaction(id: Long): Future[Seq[TransactionDetail]] = db.run {
    tableQ.filter(_.transactionId === id).result
  }

  def listByAccount(id: Long): Future[Seq[TransactionDetail]] = db.run {
    tableQ.filter(_.accountId === id).result
  }

  // to cpy
  def getById(id: Long): Future[Seq[TransactionDetail]] = db.run {
    tableQ.filter(_.id === id).result
  }


  // update required to copy
  def update(id: Long, transactionId: Long, accountId: Long, debit: Double, credit: Double): Future[Seq[TransactionDetail]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.transactionId
    db.run(q.update(transactionId))
    val q2 = for { c <- tableQ if c.id === id } yield c.accountId
    db.run(q2.update(accountId))
    val q3 = for { c <- tableQ if c.id === id } yield c.debit
    db.run(q3.update(debit))
    val q4 = for { c <- tableQ if c.id === id } yield c.credit
    db.run(q4.update(credit))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[TransactionDetail]] = db.run {
    val q = tableQ.filter(_.id === id)
    val res = q.result
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action);
    affectedRowsCount.map(s=> println(s))
    res
  }
}
