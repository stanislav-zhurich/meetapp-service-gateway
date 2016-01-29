package com.stan.meetapp.services.gateway

import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success
import scala.util.Success
import com.stan.meetapp.services.gateway.auth.AuthenticationActor
import com.stan.meetapp.services.gateway.auth.AuthenticationActor.{Authenticate, CheckToken}
import akka.actor.ActorSystem
import akka.actor.PoisonPill
import akka.actor.Props
import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.model.HttpEntity.apply
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.MethodNotAllowed
import akka.http.scaladsl.model.StatusCodes.Unauthorized
import akka.http.scaladsl.server.Directive.addByNameNullaryApply
import akka.http.scaladsl.server.Directive.addDirectiveApply
import akka.http.scaladsl.server.Directives.Segment
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Directives.enhanceRouteWithConcatenation
import akka.http.scaladsl.server.Directives.get
import akka.http.scaladsl.server.Directives.handleRejections
import akka.http.scaladsl.server.Directives.headerValueByName
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Directives.post
import akka.http.scaladsl.server.Directives.segmentStringToPathMatcher
import akka.http.scaladsl.server.MissingHeaderRejection
import akka.http.scaladsl.server.RejectionHandler
import akka.http.scaladsl.server.RequestEntityExpectedRejection
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.AuthorizationFailedRejection

/**
 * @author Stanislav Zhurich
 */
trait RestAPIRouter {
  import scala.concurrent.ExecutionContext.Implicits.global
  
  implicit def system:ActorSystem
  lazy val authenticationActor = system.actorOf(Props[AuthenticationActor], "authentication")

  val rejectionHandler = RejectionHandler.newBuilder().handle {
    case MissingHeaderRejection("auth-token") =>
      complete(HttpResponse(Unauthorized, entity = "No auth-token header provided"))
  }
  
  val authRouteRejectionHandler = RejectionHandler.newBuilder().handle {
    case MissingHeaderRejection("user") =>
      complete(HttpResponse(Unauthorized, entity = "No user header provided"))
    case MissingHeaderRejection("password") =>
      complete(HttpResponse(Unauthorized, entity = "No password header provided"))
  }
  
  def authenticationRouteProcessor = {
    post (
      headerValueByName("user"){user =>
        headerValueByName("password"){ password => ctx => 
          val response = authenticationActor.ask(Authenticate(user, password))(5 second) 
          response.flatMap {
            case Success(token) => ctx.complete(token.toString())
            case Failure(e) => ctx.reject(AuthorizationFailedRejection)
            case msg:Any => ctx.complete(msg.toString())
          }
        }
      }
    ) 
   // complete(HttpResponse(MethodNotAllowed, entity = "Only POST is supported for authentication"))
  }
  
  def routeProcessor(implicit segment:String) = {
     headerValueByName("auth-token") { token =>

           get { ctx =>
              val requestActor = system.actorOf(Props[RequestActor])
              val response = requestActor.ask(segment)(10 second)
              requestActor ! PoisonPill
              response.flatMap  {
                case Success(result) => ctx.complete(result.toString())
                case Failure(e) => ctx.reject(RequestEntityExpectedRejection)
                case msg:Any => ctx.complete(msg.toString())
              }         
           }
  }}
      
  val routes: Route =
    path("authenticate") {
        handleRejections(authRouteRejectionHandler.result().seal) {
           authenticationRouteProcessor
        }
    } ~
    path(Segment) { implicit segment =>
       routeProcessor
   }
}