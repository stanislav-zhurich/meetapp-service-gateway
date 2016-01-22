package com.stan.meetapp.services.gateway

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.model.HttpEntity.apply
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.Unauthorized
import akka.http.scaladsl.model.StatusCodes.MethodNotAllowed
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
import akka.http.scaladsl.server.Rejection
import akka.http.scaladsl.server.RejectionHandler
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteResult.route2HandlerFlow
import akka.stream.ActorMaterializer
import akka.stream.Materializer
import akka.util.Timeout

/**
 * @author Stanislav_Zhurich
 */
object Bootstrap extends App with RestAPIRouter {

  implicit val system = ActorSystem("gateway")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val timeout = new Timeout(5 seconds)
  val config = ConfigFactory.load()

  Http().bindAndHandle(routes, "0.0.0.0", 8090)

}
