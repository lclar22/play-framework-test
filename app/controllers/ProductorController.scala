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

class ProductorController @Inject() (repo: ProductorRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val productorForm: Form[CreateProductorForm] = Form {
    mapping(
      "nombre" -> nonEmptyText,
      "carnet" -> number.verifying(min(0), max(9999999)),
      "telefono" -> number.verifying(min(0), max(9999999)),
      "direccion" -> nonEmptyText,
      "cuenta" -> longNumber,
      "asociacion" -> longNumber
    )(CreateProductorForm.apply)(CreateProductorForm.unapply)
  }

  def index = Action {
    Ok(views.html.productor_index(productorForm))
  }

  def addProductor = Action.async { implicit request =>
    productorForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productor_index(errorForm)))
      },
      productor => {
        repo.create(productor.nombre, productor.carnet, productor.telefono, productor.direccion, productor.cuenta, productor.asociacion).map { _ =>
          Redirect(routes.ProductorController.index)
        }
      }
    )
  }

  def getProductores = Action.async {
  	repo.list().map { productores =>
      Ok(Json.toJson(productores))
    }
  }

  def show_view(id: Long) = Action {
    Ok(views.html.productor_index_show())
  }

  def getProductorById(id: Long) = Action.async {
    repo.getProductorById(id).map { productores =>
      Ok(Json.toJson(productores))
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
      "cuenta" -> longNumber,
      "asociacion" -> longNumber
    )(UpdateProductorForm.apply)(UpdateProductorForm.unapply)
  }

  // update required
  def index_update(id: Long) = Action.async {
    repo.getProductorById(id).map { productores =>
      val anyData = Map("id" -> id.toString().toString(), "nombre" -> productores.toList(0).nombre, "carnet" -> productores.toList(0).carnet.toString(), "telefono" -> productores.toList(0).telefono.toString(), "direccion" -> productores.toList(0).direccion, "cuenta" -> productores.toList(0).cuenta.toString(), "asociacion" -> productores.toList(0).asociacion.toString())
      Ok(views.html.productor_index_update(updateForm.bind(anyData)))
    }
  }

  // update required
  def updateProductor = Action.async { implicit request =>
    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productor_index_update(errorForm)))
      },
      productor => {
        repo.update(productor.id, productor.nombre, productor.carnet, productor.telefono, productor.direccion, productor.cuenta, productor.asociacion).map { _ =>
          Redirect(routes.ProductorController.index)
        }
      }
    )
  }

  def deleteProductor(id: Long) = Action.async {
    repo.deleteProductor(id).map { productores =>
      Ok(views.html.productor_index(productorForm))
    }
  }


}

case class CreateProductorForm(nombre: String, carnet: Int, telefono: Int, direccion: String, cuenta: Long, asociacion: Long)

case class UpdateProductorForm(id: Long, nombre: String, carnet: Int, telefono: Int, direccion: String, cuenta: Long, asociacion: Long)
