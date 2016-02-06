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

class ClienteController @Inject() (repo: ClienteRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  /**
   * The mapping for the person form.
   */
  val clienteForm: Form[CreateClienteForm] = Form {
    mapping(
      "nombre" -> nonEmptyText,
      "carnet" -> number.verifying(min(0), max(9999999)),
      "id_asociacion" -> number.verifying(min(0), max(140))
    )(CreateClienteForm.apply)(CreateClienteForm.unapply)
  }

  /**
   * The index action.
   */
  def index = Action {
    Ok(views.html.index(clienteForm))
  }

  /**
   * The add person action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on PersonRepository.
   */
  def addCliente = Action.async { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    clienteForm.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm)))
      },
      // There were no errors in the from, so create the person.
      cliente => {
        repo.create(cliente.nombre, cliente.carnet, cliente.id_asociacion).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.ClienteController.index)
        }
      }
    )
  }

  /**
   * A REST endpoint that gets all the clientes as JSON.
   */
  def getClientes = Action.async {
  	repo.list().map { clientes =>
      Ok(Json.toJson(clientes))
    }
  }
}

/**
 * The create person form.
 *
 * Generally for forms, you should define separate objects to your models, since forms very often need to present data
 * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
 * that is generated once it's created.
 */
case class CreateClienteForm(nombre: String, carnet: Int, id_asociacion: Int)
