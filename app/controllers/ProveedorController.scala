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
      "monto" -> number.verifying(min(0), max(140)),
      "cuenta" -> number.verifying(min(0), max(140)),
      "cliente" -> number.verifying(min(0), max(140))
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
        repo.create(proveedor.monto, proveedor.cuenta, proveedor.cliente).map { _ =>
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

case class CreateProveedorForm(monto: Int, cuenta: Int, cliente: Int)
