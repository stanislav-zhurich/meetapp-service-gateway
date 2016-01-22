package com.stan.meetapp.services.gateway

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
import akka.http.scaladsl.server.Route

/**
 * @author Stanislav Zhurich
 */
trait RestAPIRouter {

  val rejectionHandler = RejectionHandler.newBuilder().handle {
    case MissingHeaderRejection("auth-token") =>
      complete(HttpResponse(Unauthorized, entity = "No cookies, no service!!!"))
  }

  val routes: Route =
    path("authenticate") {
      post { ctx =>
        ctx.complete("try to authenticate")
      } ~
      complete(HttpResponse(MethodNotAllowed, entity = "Only POST is supported for authentication"))
    } ~
    path(Segment) { segment =>
       handleRejections(rejectionHandler.result().seal) {
         headerValueByName("auth-token") { token =>

           get { ctx =>
              ctx.complete("requested" + segment + "domain")
           }
         }
     }

   }
}