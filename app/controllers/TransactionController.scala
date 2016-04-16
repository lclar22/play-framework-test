package controllers

import scala.concurrent.duration._
import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import models._
import dal._
import scala.concurrent.{ ExecutionContext, Future, Await }
import scala.collection.mutable.ListBuffer
import java.util.LinkedHashMap
import collection.mutable
import scala.collection.mutable.ArrayBuffer

import javax.inject._

class TransactionController @Inject() (repo: TransactionRepository, repoVete: VeterinarioRepository, repoSto: StorekeeperRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val newForm: Form[CreateTransactionForm] = Form {
    mapping(
      "date" -> text,
      "type_1" -> text,
      "description" -> text
    )(CreateTransactionForm.apply)(CreateTransactionForm.unapply)
  }

  val unidades = scala.collection.immutable.Map[String, String]("1" -> "Unidad 1", "2" -> "Unidad 2")

  def index = Action {
    Ok(views.html.transaction_index())
  }

  def addGet = Action {
    Ok(views.html.transaction_add(newForm))
  }
  
  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.transaction_index()))
      },
      res => {
        repo.create(res.date, res.type_1, res.description).map { _ =>
          Ok(views.html.transaction_index())
        }
      }
    )
  }

  def getTransactions = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  val updateForm: Form[UpdateTransactionForm] = Form {
    mapping(
      "id" -> longNumber,
      "date" -> text,
      "type_1" -> text,
      "description" -> text
    )(UpdateTransactionForm.apply)(UpdateTransactionForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {
    Ok(views.html.transaction_show())
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    repo.getById(id).map {case (res) =>
      val anyData = Map("id" -> id.toString().toString(), "date" -> res.toList(0).date.toString(), "type_1" -> res.toList(0).type_1.toString(), "description" -> res.toList(0).description.toString())
      Ok(views.html.transaction_update(updateForm.bind(anyData)))
    }
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Ok(views.html.transaction_index())
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
        Future.successful(Ok(views.html.transaction_update(errorForm)))
      },
      res => {
        repo.update(res.id, res.date, res.type_1, res.description).map { _ =>
          Redirect(routes.TransactionController.index())
        }
      }
    )
  }
}

case class CreateTransactionForm(date: String, type_1: String, description: String)

case class UpdateTransactionForm(id: Long, date: String, type_1: String, description: String)