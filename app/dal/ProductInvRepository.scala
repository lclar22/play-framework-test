package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.ProductInv

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class ProductInvRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class ProductInvsTable(tag: Tag) extends Table[ProductInv](tag, "product_invs") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def productId = column[Long]("productId")
    def proveedorId = column[Long]("proveedorId")
    def amount = column[Int]("amount")
    def amountLeft = column[Int]("amountLeft")
    def * = (id, productId, proveedorId, amount, amountLeft) <> ((ProductInv.apply _).tupled, ProductInv.unapply)
  }

  private val tableQ = TableQuery[ProductInvsTable]

  def create(productId: Long, proveedorId: Long, amount: Int, amountLeft: Int): Future[ProductInv] = db.run {
    (tableQ.map(p => (p.productId, p.proveedorId, p.amount, p.amountLeft))
      returning tableQ.map(_.id)
      into ((nameAge, id) => ProductInv(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4))
    ) += (productId, proveedorId, amount, amountLeft)
  }

  def list(): Future[Seq[ProductInv]] = db.run {
    tableQ.result
  }

  def listByInsumo(id: Long): Future[Seq[ProductInv]] = db.run {
    tableQ.filter(_.productId === id).result
  }



    // to cpy
  def getById(id: Long): Future[Seq[ProductInv]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, productId: Long, proveedorId: Long, amount: Int, amountLeft: Int): Future[Seq[ProductInv]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.productId
    db.run(q.update(productId))
    val q2 = for { c <- tableQ if c.id === id } yield c.proveedorId
    db.run(q2.update(proveedorId))
    val q3 = for { c <- tableQ if c.id === id } yield c.amount
    db.run(q3.update(amount))
    val q4 = for { c <- tableQ if c.id === id } yield c.amountLeft
    db.run(q4.update(amountLeft))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[ProductInv]] = db.run {
    val q = tableQ.filter(_.id === id)
    val res = q.result
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action);
    affectedRowsCount.map(s=> println(s))
    res
  }
}
