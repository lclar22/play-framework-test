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

class ReporteController @Inject() (repo: ReporteRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val reporteForm: Form[CreateReporteForm] = Form {
    mapping(
      "monto" -> number.verifying(min(0), max(140)),
      "cuenta" -> number.verifying(min(0), max(140)),
      "cliente" -> number.verifying(min(0), max(140))
    )(CreateReporteForm.apply)(CreateReporteForm.unapply)
  }

  def index = Action {
    Ok(views.html.reporte_index(reporteForm))
  }

  def addReporte = Action.async { implicit request =>
    reporteForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.reporte_index(errorForm)))
      },
      reporte => {
        repo.create(reporte.monto, reporte.cuenta, reporte.cliente).map { _ =>
          Redirect(routes.ReporteController.index)
        }
      }
    )
  }

  def getReportes = Action.async {
  	repo.list().map { reportes =>
      Ok(Json.toJson(reportes))
    }
  }
}

case class CreateReporteForm(monto: Int, cuenta: Int, cliente: Int)
