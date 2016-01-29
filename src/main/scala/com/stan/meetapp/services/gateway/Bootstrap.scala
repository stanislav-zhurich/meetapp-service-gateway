package com.stan.meetapp.services.gateway

import scala.concurrent.duration.DurationInt

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteResult.route2HandlerFlow
import akka.stream.ActorMaterializer
import akka.util.Timeout

/**
 * @author Stanislav_Zhurich
 */
object Bootstrap extends App with RestAPIRouter {

  override implicit val system = ActorSystem("gateway")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val config = ConfigFactory.load()

  Http().bindAndHandle(routes, "0.0.0.0", 8090)

}
