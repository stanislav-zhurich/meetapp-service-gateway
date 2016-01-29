package com.stan.meetapp.services.gateway.auth

import java.util.UUID

import akka.actor.Actor

/**
 * actor is responsible for user authentication
 */
private[gateway] object AuthenticationActor{
  case class Authenticate(username:String, password:String)
  case class CheckToken(token:String)
  
}

private[gateway] class AuthenticationActor extends Actor{
  import AuthenticationActor._
  
  val tokens = scala.collection.mutable.Map[String, String]()
 
  def receive ={
    case Authenticate(username, password) => {
      val token = generateToken(username, password)
      tokens += (token -> username)
      sender ! token
    }
    case CheckToken(token) => {      
      tokens.get(token) match {
        case Some(value) => sender ! value
        case None => sender ! akka.actor.Status.Failure(new IllegalArgumentException)
      }
    }
    
      
  }
  
  private def generateToken(username:String, password:String) = {
    UUID.randomUUID().toString()
  }
}