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
                                          repoSto: UserRepository, repoInsUser: UserRepository,
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

  var veterinariosNames = getVeterinarioListNamesMap()
  var storeNames = getStorekeepersNamesMap()

  def index = Action { implicit request =>
    if (request.session.get("role").getOrElse("0").toLowerCase == "veterinario") {
      veterinariosNames = getVeterinarioNamesMap(request.session.get("userId").getOrElse("0").toLong)
    } else {
      veterinariosNames = getVeterinarioListNamesMap()
    }
    storeNames = getStorekeepersNamesMap()
    Ok(views.html.productRequest_index(veterinariosNames, storeNames))
  }

  def addGet = Action { implicit request =>
    if (request.session.get("role").getOrElse("0").toLowerCase == "veterinario") {
      veterinariosNames = getVeterinarioNamesMap(request.session.get("userId").getOrElse("0").toLong)
    } else {
      veterinariosNames = getVeterinarioListNamesMap()
    }
    storeNames = getStorekeepersNamesMap()
    Ok(views.html.productRequest_add(newForm, veterinariosNames, storeNames))
  }

  def add = Action.async { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.productRequest_index(Map[String, String](), Map[String, String]())))
      },
      res => {
        repo.create(res.date, res.veterinario, veterinariosNames(res.veterinario.toString), res.storekeeper, storeNames(res.storekeeper.toString), res.status, res.detail, "veterinaria").map { _ =>
          Redirect(routes.VeterinarioController.profile(res.veterinario))
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
  def getUpdate(id: Long) = Action.async { implicit request =>
    repo.getById(id).map {case (res) =>
      val anyData = Map("id" -> id.toString().toString(), "date" -> res.toList(0).date.toString(), "veterinario" -> res.toList(0).veterinario.toString(), "storekeeper" -> res.toList(0).storekeeper.toString(), "status" -> res.toList(0).status.toString(), "detail" -> res.toList(0).detail.toString())
      if (request.session.get("role").getOrElse("0").toLowerCase == "veterinario") {
        veterinariosNames = getVeterinarioNamesMap(request.session.get("userId").getOrElse("0").toLong)
      } else {
        veterinariosNames = getVeterinarioListNamesMap()
      }
      storeNames = getStorekeepersNamesMap()
      Ok(views.html.productRequest_update(updateForm.bind(anyData), veterinariosNames, storeNames))
    }
  }

// update required
  def getSend(id: Long) = Action.async { implicit request =>
    repo.sendById(id).map {case (res) =>
      Redirect(routes.UserController.profileById(request.session.get("userId").getOrElse("0").toLong))
    }
  }

// update required
  def getAccept(id: Long) = Action.async { implicit request =>
    repo.acceptById(id).map {case (res) =>
      Redirect(routes.UserController.profileById(request.session.get("userId").getOrElse("0").toLong))
    }
  }

// update required
  def getFinish(id: Long) = Action.async { implicit request =>
    repo.finishById(id).map {case (res) =>
      Redirect(routes.UserController.profileById(request.session.get("userId").getOrElse("0").toLong))
    }
  }


  def getVeterinarioNamesMap(id: Long): Map[String, String] = {
    Await.result(repoVete.getById(id).map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach { user => 
        cache put (user.id.toString, user.nombre)
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  def getVeterinarioListNamesMap(): Map[String, String] = {
    Await.result(repoVete.listVeterinarios().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach { user => 
        cache put (user.id.toString, user.nombre)
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  def getInsumoUserNamesMap(): Map[String, String] = {
    Await.result(repoInsUser.listInsumoUsers().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach { user => 
        cache put (user.id.toString, user.nombre)
      }
      println(cache)
      cache.toMap
    }, 3000.millis)
  }

  def getStorekeepersNamesMap(): Map[String, String] = {
    Await.result(repoSto.listStorekeepers().map{ case (res1) => 
      val cache = collection.mutable.Map[String, String]()
      res1.foreach { user => 
        cache put (user.id.toString, user.nombre)
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
        repo.update(
                    res.id, res.date, res.veterinario, veterinariosNames(res.veterinario.toString()),
                    res.storekeeper, storeNames(res.storekeeper.toString), res.status, res.detail, "veterinaria"
                    ).map { _ =>
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
        repo.update(
                      res.id, res.date, res.veterinario, veterinariosNames(res.veterinario.toString),
                      res.storekeeper, storeNames(res.storekeeper.toString), res.status, res.detail, "insumo"
                    ).map { _ =>
          Redirect(routes.ProductRequestController.index)
        }
      }
    )
  }
}

case class CreateProductRequestForm(date: String, veterinario: Long, storekeeper: Long, status: String, detail: String)

case class UpdateProductRequestForm(id: Long, date: String, veterinario: Long, storekeeper: Long, status: String, detail: String)