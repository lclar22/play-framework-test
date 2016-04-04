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
import it.innove.play.pdf.PdfGenerator

class DiscountReportController @Inject() (repo: DiscountReportRepository, repoProd: ProductorRepository, 
                                          repoSto: StorekeeperRepository, repoRequestRows: RequestRowRepository, 
                                          val messagesApi: MessagesApi)
                                         (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val newForm: Form[CreateDiscountReportForm] = Form {
    mapping(
      "startDate" -> text,
      "endDate" -> text,
      "status" -> text,
      "total" -> number
    )(CreateDiscountReportForm.apply)(CreateDiscountReportForm.unapply)
  }

  val unidades = scala.collection.immutable.Map[String, String]("1" -> "Unidad 1", "2" -> "Unidad 2")

  def index = Action {
    val productoresNames = getProductoresNamesMap()
    Ok(views.html.discountReport_index(productoresNames))
  }

  def generarReporte(id: Long) = Action {
    val productRequestRowsByProduct = getProductoRequestsByProductor()
    println("Hello this is the example")
    println(productRequestRowsByProduct)
    Ok(views.html.discountReport_index(productRequestRowsByProduct))
  }

  def addGet = Action {
    val productoresNames = getProductoresNamesMap()
    Ok(views.html.discountReport_add(newForm, productoresNames))
  }
  
  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.discountReport_index(Map[String, String]())))
      },
      res => {
        repo.create(res.startDate, res.endDate, res.status, res.total).map { _ =>
          Redirect(routes.DiscountReportController.index)
        }
      }
    )
  }

  def getDiscountReports = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  val updateForm: Form[UpdateDiscountReportForm] = Form {
    mapping(
      "id" -> longNumber,
      "startDate" -> text,
      "endDate" -> text,
      "status" -> text,
      "total" -> number
    )(UpdateDiscountReportForm.apply)(UpdateDiscountReportForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {
    Ok(views.html.discountReport_show())
  }

  def show_pdf(id: Long) = Action {
    val generator = new PdfGenerator
    Ok(generator.toBytes(views.html.discountReport_show_pdf(), "http://localhost:9000/")).as("application/pdf")
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    repo.getById(id).map {case (res) =>
      val anyData = Map("id" -> id.toString(), "startDate" -> res.toList(0).startDate.toString(), "endDate" -> res.toList(0).endDate.toString(), "status" -> res.toList(0).status.toString(), "total" -> res.toList(0).total.toString())
      val productoresMap = getProductoresNamesMap()
      Ok(views.html.discountReport_update(updateForm.bind(anyData), productoresMap))
    }
  }

  def getProductoresNamesMap(): Map[String, String] = {
    Await.result(repoProd.getListNames().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach{ case (key: Long, value: String) => 
        cache put (key.toString(), value)
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  def getProductoRequestsByProductor(): Map[String, String] = {
    Await.result(repoRequestRows.listByQuantity().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach{ case (requestRow) => 
        cache put (requestRow.productorId.toString(), requestRow.productorId.toString())
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Ok(views.html.discountReport_index(Map[String, String]()))
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
        Future.successful(Ok(views.html.discountReport_update(errorForm, Map[String, String]())))
      },
      res => {
        repo.update(res.id, res.startDate, res.endDate, res.status, res.total).map { _ =>
          Redirect(routes.DiscountReportController.index)
        }
      }
    )
  }

}

case class CreateDiscountReportForm(startDate: String, endDate: String, status: String, total: Int)

case class UpdateDiscountReportForm(id: Long, startDate: String, endDate: String, status: String, total: Int)