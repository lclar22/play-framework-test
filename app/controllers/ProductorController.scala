package controllers

import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import models._
import dal._

import scala.concurrent.{ ExecutionContext, Future }

import javax.inject._
import it.innove.play.pdf.PdfGenerator

class ProductorController @Inject() (repo: ProductorRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val asociaciones = scala.collection.immutable.Map[String, String]("1" -> "Asociacion 1", "2" -> "Asociacion 2")

  val newForm: Form[CreateProductorForm] = Form {
    mapping(
      "nombre" -> nonEmptyText,
      "carnet" -> number.verifying(min(0), max(9999999)),
      "telefono" -> number.verifying(min(0), max(9999999)),
      "direccion" -> nonEmptyText,
      "account" -> longNumber,
      "asociacion" -> longNumber
    )(CreateProductorForm.apply)(CreateProductorForm.unapply)
  }

  def index = Action {
    Ok(views.html.productor_index(newForm, asociaciones))
  }

  def index_pdf = Action {
    val generator = new PdfGenerator
    Ok(generator.toBytes(views.html.reporte_productores(), "http://localhost:9000/")).as("application/pdf")
  }



  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productor_index(errorForm, asociaciones)))
      },
      productor => {
        repo.create(productor.nombre, productor.carnet, productor.telefono, productor.direccion, productor.account, productor.asociacion).map { _ =>
          Redirect(routes.ProductorController.index)
        }
      }
    )
  }

  def getProductorsByModule(id: Long) = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }


  def getProductores = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getProductoresReport = Action.async {
  	repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  val updateForm: Form[UpdateProductorForm] = Form {
    mapping(
      "id" -> longNumber,
      "nombre" -> nonEmptyText,
      "carnet" -> number.verifying(min(0), max(9999999)),
      "telefono" -> number.verifying(min(0), max(9999999)),
      "direccion" -> nonEmptyText,
      "account" -> longNumber,
      "asociacion" -> longNumber,
      "totalDebt" -> longNumber,
      "numberPayment" -> number,
      "position" -> text
    )(UpdateProductorForm.apply)(UpdateProductorForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {
    Ok(views.html.productor_show())
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    repo.getById(id).map { res =>
      val anyData = Map("id" -> id.toString().toString(), "nombre" -> res.toList(0).nombre, "carnet" -> res.toList(0).carnet.toString(), "telefono" -> res.toList(0).telefono.toString(), "direccion" -> res.toList(0).direccion, "account" -> res.toList(0).account.toString(), "asociacion" -> res.toList(0).asociacion.toString())
      Ok(views.html.productor_update(updateForm.bind(anyData)))
    }
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Ok(views.html.productor_index(newForm, asociaciones))
    }
  }

  // to copy
  def getById(id: Long) = Action.async {
    repo.getById(id).map { res =>
      Ok(Json.toJson(res))
    }
  }


  // update required
  def updatePost = Action.async { implicit request =>
    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productor_update(errorForm)))
      },
      productor => {
        repo.update(productor.id, productor.nombre, productor.carnet, productor.telefono, productor.direccion, productor.account, productor.asociacion, productor.totalDebt, productor.numberPayment, productor.position).map { _ =>
          Redirect(routes.ProductorController.index)
        }
      }
    )
  }

}

case class CreateProductorForm(nombre: String, carnet: Int, telefono: Int, direccion: String, account: Long, asociacion: Long)

// Update required
case class UpdateProductorForm(id: Long, nombre: String, carnet: Int, telefono: Int, direccion: String, account: Long, asociacion: Long, totalDebt: Long, numberPayment: Int, position: String)
