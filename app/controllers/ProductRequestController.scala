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

class ProductRequestController @Inject() (repo: ProductRequestRepository, repoVete: UserRepository,
                                          repoSto: StorekeeperRepository, repoInsUser: InsumoUserRepository,
                                          val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val newForm: Form[CreateProductRequestForm] = Form {
    mapping(
      "date" -> text,
      "veterinario" -> longNumber,
      "storekeeper" -> longNumber,
      "status" -> text,
      "detail" -> text
    )(CreateProductRequestForm.apply)(CreateProductRequestForm.unapply)
  }

  val unidades = scala.collection.immutable.Map[String, String]("1" -> "Unidad", "2" -> "Caja")

  def index = Action {
    val veterinariosNames = getVeterinarioNamesMap()
    val storeNames = getStorekeepersNamesMap()
    Ok(views.html.productRequest_index(veterinariosNames, storeNames))
  }

  def addGet = Action {
    val veterinariosNames = getVeterinarioNamesMap()
    val storeNames = getStorekeepersNamesMap()
    Ok(views.html.productRequest_add(newForm, veterinariosNames, storeNames))
  }

  def addGetByInsumo = Action {
    val insumoUsersNames = getInsumoUserNamesMap()
    val storeNames = getStorekeepersNamesMap()
    Ok(views.html.productRequestByInsumo_add(newForm, insumoUsersNames, storeNames))
  }

  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productRequest_index(Map[String, String](), Map[String, String]())))
      },
      res => {
        repo.create(res.date, res.veterinario, res.storekeeper, res.status, res.detail, "veterinaria").map { _ =>
          Redirect(routes.VeterinarioController.profile(res.veterinario))
        }
      }
    )
  }

  def addByInsumo = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productRequest_index(Map[String, String](), Map[String, String]())))
      },
      res => {
        repo.create(res.date, res.veterinario, res.storekeeper, res.status, res.detail, "insumo").map { _ =>
          Redirect(routes.InsumoUserController.profile(res.veterinario))
        }
      }
    )
  }

  def getProductRequestsByVeterinario(id: Long) = Action.async {
    repo.listByVeterinario(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getProductRequestsByStorekeeper(id: Long) = Action.async {
    repo.listByStorekeeper(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getProductRequestsByInsumoUser(id: Long) = Action.async {
    repo.listByInsumoUser(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getProductRequests = Action.async {
    repo.list().map { res =>
      Ok(Json.toJson(res))
    }
  }


  // update required
  val updateForm: Form[UpdateProductRequestForm] = Form {
    mapping(
      "id" -> longNumber,
      "date" -> text,
      "veterinario" -> longNumber,
      "storekeeper" -> longNumber,
      "status" -> text,
      "detail" -> text
    )(UpdateProductRequestForm.apply)(UpdateProductRequestForm.unapply)
  }

  // to copy
  def show(id: Long) = Action {
    Ok(views.html.productRequest_show())
  }

  // to copy
  def showByInsumo(id: Long) = Action {
    Ok(views.html.productRequestByInsumo_show())
  }

  // update required
  def getUpdate(id: Long) = Action.async {
    repo.getById(id).map {case (res) =>
      val anyData = Map("id" -> id.toString().toString(), "date" -> res.toList(0).date.toString(), "veterinario" -> res.toList(0).veterinario.toString(), "storekeeper" -> res.toList(0).storekeeper.toString(), "status" -> res.toList(0).status.toString(), "detail" -> res.toList(0).detail.toString())
      val insumosMap = getVeterinarioNamesMap()
      val storeMap = getStorekeepersNamesMap()
      Ok(views.html.productRequest_update(updateForm.bind(anyData), insumosMap, storeMap))
    }
  }

  // update required
  def getUpdateByInsumo(id: Long) = Action.async {
    repo.getById(id).map {case (res) =>
      val anyData = Map("id" -> id.toString().toString(), "date" -> res.toList(0).date.toString(), "veterinario" -> res.toList(0).veterinario.toString(), "storekeeper" -> res.toList(0).storekeeper.toString(), "status" -> res.toList(0).status.toString(), "detail" -> res.toList(0).detail.toString())
      val insumosMap = getVeterinarioNamesMap()
      val storeMap = getStorekeepersNamesMap()
      Ok(views.html.productRequestByInsumo_update(updateForm.bind(anyData), insumosMap, storeMap))
    }
  }

// update required
  def getSend(id: Long) = Action.async {
    repo.sendById(id).map {case (res) =>
      Redirect(routes.VeterinarioController.profile(res.toList(0).veterinario))
    }
  }

// update required
  def getAccept(id: Long) = Action.async {
    repo.acceptById(id).map {case (res) =>
      Redirect(routes.VeterinarioController.profile(res.toList(0).veterinario))
    }
  }

// update required
  def getFinish(id: Long) = Action.async {
    repo.finishById(id).map {case (res) =>
      Redirect(routes.VeterinarioController.profile(res.toList(0).veterinario))
    }
  }


  def getVeterinarioNamesMap(): Map[String, String] = {
    Await.result(repoVete.getListNames().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach{ case (key: Long, value: String) => 
        cache put (key.toString(), value)
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  def getInsumoUserNamesMap(): Map[String, String] = {
    Await.result(repoInsUser.getListNames().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach{ case (key: Long, value: String) => 
        cache put (key.toString(), value)
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  def getStorekeepersNamesMap(): Map[String, String] = {
    Await.result(repoSto.getListNames().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach{ case (key: Long, value: String) => 
        cache put (key.toString(), value)
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  // delete required
  def delete(id: Long) = Action.async {
    repo.delete(id).map { res =>
      Ok(views.html.productRequest_index(Map[String, String](), Map[String, String]()))
    }
  }

  // to copy
  def getById(id: Long) = Action.async {
    repo.getById(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  // update required
  def updatePostVeterinaria = Action.async { implicit request =>
    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productRequest_update(errorForm, Map[String, String](), Map[String, String]())))
      },
      res => {
        repo.update(res.id, res.date, res.veterinario, res.storekeeper, res.status, res.detail, "veterinaria").map { _ =>
          Redirect(routes.ProductRequestController.index)
        }
      }
    )
  }

  // update required
  def updatePostInsumo = Action.async { implicit request =>
    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productRequest_update(errorForm, Map[String, String](), Map[String, String]())))
      },
      res => {
        repo.update(res.id, res.date, res.veterinario, res.storekeeper, res.status, res.detail, "insumo").map { _ =>
          Redirect(routes.ProductRequestController.index)
        }
      }
    )
  }
}

case class CreateProductRequestForm(date: String, veterinario: Long, storekeeper: Long, status: String, detail: String)

case class UpdateProductRequestForm(id: Long, date: String, veterinario: Long, storekeeper: Long, status: String, detail: String)