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

class ProveedorController @Inject() (repo: ProveedorRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val proveedorForm: Form[CreateProveedorForm] = Form {
    mapping(
      "nombre" -> nonEmptyText,
      "telefono" -> number,
      "direccion" -> nonEmptyText,
      "contacto" -> nonEmptyText,
      "cuenta" -> longNumber
    )(CreateProveedorForm.apply)(CreateProveedorForm.unapply)
  }

  def index = Action {
    Ok(views.html.proveedor_index(proveedorForm))
  }

  def addProveedor = Action.async { implicit request =>
    proveedorForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.proveedor_index(errorForm)))
      },
      proveedor => {
        repo.create(proveedor.nombre, proveedor.telefono, proveedor.direccion, proveedor.contacto, proveedor.cuenta).map { _ =>
          Redirect(routes.ProveedorController.index)
        }
      }
    )
  }

  def getProveedores = Action.async {
    repo.list().map { proveedores =>
      Ok(Json.toJson(proveedores))
    }
  }
}

case class CreateProveedorForm(nombre: String, telefono: Int, direccion: String, contacto: String, cuenta: Long)
