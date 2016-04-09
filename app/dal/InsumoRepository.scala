package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import models.Insumo

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class InsumoRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class InsumosTable(tag: Tag) extends Table[Insumo](tag, "insumos") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def nombre = column[String]("nombre")
    def costo = column[Int]("costo")
    def porcentage = column[Int]("porcentage")
    def descripcion = column[String]("descripcion")
    def unidad = column[Long]("unidad")
    def currentAmount = column[Int]("currentAmount")
    def * = (id, nombre, costo, porcentage, descripcion, unidad, currentAmount) <> ((Insumo.apply _).tupled, Insumo.unapply)
  }

  private val tableQ = TableQuery[InsumosTable]

  def create(nombre: String, costo: Int, porcentage: Int, descripcion: String, unidad: Long, currentAmount: Int): Future[Insumo] = db.run {
    (tableQ.map(p => (p.nombre, p.costo, p.porcentage, p.descripcion, p.unidad, p.currentAmount))
      returning tableQ.map(_.id)
      into ((nameAge, id) => Insumo(id, nameAge._1, nameAge._2, nameAge._3, nameAge._4, nameAge._5, nameAge._6))
    ) += (nombre, costo, porcentage, descripcion, unidad, currentAmount)
  }

  def list(): Future[Seq[Insumo]] = db.run {
    tableQ.result
  }

  def getListNames(): Future[Seq[(Long, String)]] = db.run {
    tableQ.filter(_.id < 10L).map(s => (s.id, s.nombre)).result
  }

    // to cpy
  def getById(id: Long): Future[Seq[Insumo]] = db.run {
    tableQ.filter(_.id === id).result
  }

  // update required to copy
  def update(id: Long, nombre: String, costo: Int, porcentage: Int, descripcion: String, unidad: Long, currentAmount: Int): Future[Seq[Insumo]] = db.run {
    val q = for { c <- tableQ if c.id === id } yield c.nombre
    db.run(q.update(nombre))
    val q2 = for { c <- tableQ if c.id === id } yield c.porcentage
    db.run(q2.update(porcentage))
    val q3 = for { c <- tableQ if c.id === id } yield c.costo
    db.run(q3.update(costo))
    val q4 = for { c <- tableQ if c.id === id } yield c.descripcion
    db.run(q4.update(descripcion))
    val q5 = for { c <- tableQ if c.id === id } yield c.unidad
    db.run(q5.update(unidad))
    val q6 = for { c <- tableQ if c.id === id } yield c.currentAmount
    db.run(q6.update(currentAmount))
    tableQ.filter(_.id === id).result
  }

  // delete required
  def delete(id: Long): Future[Seq[Insumo]] = db.run {
    val q = tableQ.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    println("removed " + affectedRowsCount);
    tableQ.result
  }

  def updateAmount(insumoId: Long, amount: Int) = {
    val q = for { c <- tableQ if c.id === insumoId } yield c.currentAmount
    db.run(tableQ.filter(_.id === insumoId).result).map(s=> s.map(insumoObj => 
      db.run(q.update(amount + insumoObj.currentAmount))
    ))
  }

  def updateInventary(insumoId: Long, amount: Int) = {
    val q = for { c <- tableQ if c.id === insumoId } yield c.currentAmount
    db.run(tableQ.filter(_.id === insumoId).result).map(s=> s.map(insumoObj => 
      db.run(q.update(amount + insumoObj.currentAmount))
    ))
  }

}
