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

import javax.inject._
import it.innove.play.pdf.PdfGenerator
import security.MyDeadboltHandler


class LoginController @Inject() (repo: UserRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val newForm: Form[LoginForm] = Form {
    mapping(
      "user" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)
  }

  def index = Action {
    Ok(views.html.login(newForm))
  }

  def logout = Action {
    Ok("Bye").withNewSession
  }

  def login = Action { implicit request =>
    newForm.bindFromRequest.fold(
      errorForm => {
        Ok(views.html.login(errorForm))
      },
      res => {
        Await.result(repo.getByLogin(res.user, res.password).map { res2 =>
          if (res2.length > 0) {
            Ok("Welcome!").withSession("userSecurity" -> res2(0).nombre, "userSecurity2" -> res2(0).nombre)
            if (res2(0).type_1 == "Admin") {
              Redirect("/")
            } else if (res2(0).type_1 == "veterinario") {
              Redirect(routes.VeterinarioController.profile(res2(0).id))
            } else if (res2(0).type_1 == "Insumo") {
              Redirect(routes.InsumoUserController.profile(res2(0).id))
            } else if (res2(0).type_1 == "Almacen") {
              Redirect(routes.StorekeeperController.profile(res2(0).id))
            } else {
              Ok(views.html.storekeeper_profile2(res2(0)))
              Redirect("/error")
            }
          } else {
            Ok(views.html.login(newForm))
          }
        }, 3000.millis)
        /*repo.getByLogin(res.user, res.password).map { res2 =>
          if (res2.length > 0) {
            Ok("Welcome!").withSession("userSecurity" -> res2(0).nombre, "userSecurity2" -> res2(0).nombre)
            if (res2(0).type_1 == "Admin") {
              Ok(views.html.index(new MyDeadboltHandler))
            } else if (res2(0).type_1 == "Veterinario") {
              Ok(views.html.veterinario_profile2(res2(0)))
            } else if (res2(0).type_1 == "Insumo") {
              Ok(views.html.insumo_profile2(res2(0)))
            } else if (res2(0).type_1 == "Almacen") {
              Ok(views.html.storekeeper_profile2(res2(0)))
            }
          } else {
            println("NO logged");
            Ok(views.html.login(newForm))
          }
        }*/
      }
    )
  }

}

case class LoginForm(user: String, password: String)
