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
      "fecha" -> nonEmptyText,
      "descripcion" -> text
    )(CreateTransaccionForm.apply)(CreateTransaccionForm.unapply)
  }

  def index = Action {
    Ok(views.html.transaccion_add(transaccionForm))
  }

  def addTransaccion = Action.async { implicit request =>
    transaccionForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.transaccion_add(errorForm)))
      },
      transaccion => {
        repo.create(transaccion.fecha, transaccion.descripcion).map { _ =>
          Redirect(routes.TransaccionController.getTransaccionListView)
        }
      }
    )
  }

  def getTransaccionListView = Action {
    Ok(views.html.transaccion_table())
  }
  
  def getTransaccionView = Action {
    Ok(views.html.transaccion_view())
  }

  def getTransacciones = Action.async {
  	repo.list().map { transacciones =>
      Ok(Json.toJson(transacciones))
    }
  }
}

case class CreateTransaccionForm(fecha: String, descripcion: String)
