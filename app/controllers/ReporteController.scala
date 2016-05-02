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

import scala.concurrent.{ Future, ExecutionContext, Await }

import javax.inject._

class ReporteController @Inject() (repo: ReporteRepository, repoAccount: AccountRepository
                                   , repoTransDetails: TransactionDetailRepository, repoTrans: TransactionRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val newForm: Form[CreateReporteForm] = Form {
    mapping(
      "monto" -> number.verifying(min(0), max(140)),
      "account" -> number.verifying(min(0), max(140)),
      "cliente" -> number.verifying(min(0), max(140))
    )(CreateReporteForm.apply)(CreateReporteForm.unapply)
  }

  def index = Action {
    Ok(views.html.reporte_index(newForm))
  }

  def balance = Action {
    val activos = getByActivo()
    val pasivos = getByPasivo()
    val patrimonios = Seq[Account]()
    Ok(views.html.reporte_balance(activos, pasivos, patrimonios))
  }

  def diary = Action {
    val transactions = getTransactions()
    transactions.foreach { transaction => 
      Await.result(repoTransDetails.listByTransaction(transaction.id).map {
        res =>  
        transaction.details = res;
      }, 500.millis)
    }
    Ok(views.html.reporte_diary(transactions))
  }


  def mayor = Action {
    val details = getTransactionDetails()
    Ok(views.html.reporte_mayor(details))
  }

  def getTransactionDetails(): Seq[TransactionDetail] = {
    Await.result(repoTransDetails.list().map {
      res => res;
    }, 1000.millis)
  }

  def getTransactions(): Seq[Transaction] = {
    Await.result(repoTrans.getListPopulated().map {
      res => res
    }, 1000.millis)
  }

  def getByActivo(): Seq[Account] = {
    Await.result(repoAccount.getByActivo().map {
      res => res;
    }, 1000.millis)
  }

  def getByPasivo(): Seq[Account] = {
    Await.result(repoAccount.getByPasivo().map {
      res => res;
    }, 1000.millis)
  }


  def addReporte = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.reporte_index(errorForm)))
      },
      reporte => {
        repo.create(reporte.monto, reporte.account, reporte.cliente).map { _ =>
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

case class CreateReporteForm(monto: Int, account: Int, cliente: Int)
