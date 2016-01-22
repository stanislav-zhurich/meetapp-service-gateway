package com.stan.meetapp.services.gateway

import akka.actor.Actor
import com.stan.meetapp.services.common.api.ActorCreationSupport
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
 *
 * @author Stanislav Zhurich
 *
 */
class RequestActor extends Actor with ActorCreationSupport {

  def receive = {
    case message => path("test"){
                        complete("boom")
                    }
  }
}