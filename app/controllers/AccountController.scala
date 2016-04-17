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

class AccountController @Inject() (repo: AccountRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val asociaciones = scala.collection.immutable.Map[String, String]("1" -> "Asociacion 1", "2" -> "Asociacion 2")

  val newForm: Form[CreateAccountForm] = Form {
    mapping(
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "type_1" -> nonEmptyText,
      "description" -> nonEmptyText
    )(CreateAccountForm.apply)(CreateAccountForm.unapply)
  }

  def index = Action {
    Ok(views.html.account_index(newForm, asociaciones))
  }

  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.account_index(errorForm, asociaciones)))
      },
      res => {
        repo.create(res.code, res.name, res.type_1, res.description).map { _ =>
          Redirect(routes.AccountController.index)
        }
      }
    )
  }

  def getAccountsByModule(id: Long) = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }


  def getAccounts = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getAccountsReport = Action.async {
  	repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  val updateForm: Form[UpdateAccountForm] = Form {
    mapping(
      "id" -> longNumber,
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "type_1" -> nonEmptyText,
      "description" -> nonEmptyText
    )(UpdateAccountForm.apply)(UpdateAccountForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {
    Ok(views.html.account_show())
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    repo.getById(id).map { res =>
      val anyData = Map("id" -> id.toString().toString(), "code" -> res.toList(0).code, "name" -> res.toList(0).name.toString(), "type_1" -> res.toList(0).type_1.toString(), "description" -> res.toList(0).description)
      Ok(views.html.account_update(updateForm.bind(anyData)))
    }
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Ok(views.html.account_index(newForm, asociaciones))
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
        Future.successful(Ok(views.html.account_update(errorForm)))
      },
      res => {
        repo.update(res.id, res.code, res.name, res.type_1, res.description).map { _ =>
          Redirect(routes.AccountController.index)
        }
      }
    )
  }

}

case class CreateAccountForm(code: String, name: String, type_1: String, description: String)

// Update required
case class UpdateAccountForm(id: Long, code: String, name: String, type_1: String, description: String)
