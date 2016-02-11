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

class TransaccionController @Inject() (repo: TransaccionRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val transaccionForm: Form[CreateTransaccionForm] = Form {
    mapping(
      "monto" -> number.verifying(min(0), max(140)),
      "cuenta" -> number.verifying(min(0), max(140)),
      "cliente" -> number.verifying(min(0), max(140))
    )(CreateTransaccionForm.apply)(CreateTransaccionForm.unapply)
  }

  def index = Action {
    Ok(views.html.cliente_transaccion(transaccionForm))
  }

  def addTransaccion = Action.async { implicit request =>
    transaccionForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.cliente_transaccion(errorForm)))
      },
      transaccion => {
        repo.create(transaccion.monto, transaccion.cuenta, transaccion.cliente).map { _ =>
          Redirect(routes.TransaccionController.index)
        }
      }
    )
  }

  def getTransacciones = Action.async {
  	repo.list().map { transacciones =>
      Ok(Json.toJson(transacciones))
    }
  }
}

case class CreateTransaccionForm(monto: Int, cuenta: Int, cliente: Int)
