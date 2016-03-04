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

class BancoController @Inject() (repo: BancoRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val bancoForm: Form[CreateBancoForm] = Form {
    mapping(
      "nombre" -> nonEmptyText,
      "tipo" -> nonEmptyText
    )(CreateBancoForm.apply)(CreateBancoForm.unapply)
  }

  // update required
  val updateForm: Form[UpdateBancoForm] = Form {
    mapping(
      "id" -> longNumber,
      "nombre" -> nonEmptyText,
      "tipo" -> nonEmptyText
    )(UpdateBancoForm.apply)(UpdateBancoForm.unapply)
  }

  // update required
  def index_update(id: Long) = Action.async {
    repo.getCuentaById(id).map { bancos =>
      val anyData = Map("id" -> id.toString(), "nombre" -> bancos.toList(0).nombre, "tipo" ->bancos.toList(0).tipo)
      Ok(views.html.banco_index_update(updateForm.bind(anyData)))
    }
  }

  def index() = Action {
    Ok(views.html.banco_index(bancoForm))
  }

  def addBanco = Action.async { implicit request =>
    bancoForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.banco_index(errorForm)))
      },
      banco => {
        repo.create(banco.nombre, banco.tipo).map { _ =>
          Redirect(routes.BancoController.index)
        }
      }
    )
  }

  // update required
  def updateBanco = Action.async { implicit request =>
    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.banco_index_update(errorForm)))
      },
      banco => {
        repo.update(banco.id, banco.nombre, banco.tipo).map { _ =>
          Redirect(routes.BancoController.index)
        }
      }
    )
  }

  def getBancos = Action.async {
    repo.list().map { bancos =>
      Ok(Json.toJson(bancos))
    }
  }

  def list = Action.async {
    repo.list().map { bancos =>
      Ok(Json.toJson(bancos))
    }
  }
    
  def getBankNames = Action.async {
    repo.listNames().map { bancos =>
      Ok(Json.toJson(bancos))
    }
  }

  def getNameListByName = Action.async {
    repo.getNameListByName(nombre = "Luis").map { bancos =>
      Ok(Json.toJson(bancos))
    }
  }

  def getExampleAction(name: String) = Action { request =>
    Ok("Got request [" + name + "]")
  }

  def getExampleAction_3(name: String) = Action { request =>
    Ok("Got request [" + name + "]")
  }

  def getExampleAction_2(name: String) = Action {
    Redirect("/cuenta_example_action?name=" + name)
  }

  def getCuentaById(id: Long) = Action.async {
    repo.getCuentaById(id).map { bancos =>
      Ok(Json.toJson(bancos))
    }
  }

  def show_view(id: Long) = Action {
    Ok(views.html.banco_index_show())
  }

}

case class CreateBancoForm(nombre: String, tipo: String)

// update required
case class UpdateBancoForm(id: Long, nombre: String, tipo: String)
