package com.stan.meetapp.services.gateway

import scala.concurrent.ExecutionContextExecutor
import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.Materializer
import akka.http.javadsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.HttpResponse
import akka.stream.scaladsl.Flow
/**
 * @author Stanislav_Zhurich
 */
object Bootstrap extends App {

  implicit val system = ActorSystem("gateway")
  implicit val materializer = ActorMaterializer()
 
   val handler:HttpRequest => HttpResponse = {
    case _:HttpRequest => HttpResponse(entity = "PONG!")
  }
 
  Http().bind( "0.0.0.0", 8090).runForeach(connection => {
    println("connection accepted:" + connection.remoteAddress)
    connection handleWith { Flow[HttpRequest] map handler }
  })

}