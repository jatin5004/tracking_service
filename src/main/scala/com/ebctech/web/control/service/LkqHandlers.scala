package com.ebctech.web.control.service


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, StatusCodes, headers}
import akka.http.scaladsl.unmarshalling.Unmarshal
import ch.qos.logback.classic.Logger
import com.ebctech.web.control.actor.{Tracking, TrackingId, TrackingResponse}
import com.ebctech.web.control.db.DataBaseService
import org.slf4j.LoggerFactory
import spray.json.{DefaultJsonProtocol, JsObject}

import java.net.URL
import spray.json._

import java.io.{BufferedInputStream, File, FileOutputStream, IOException}
import java.nio.file.{Files, Paths}
import java.time.{LocalDate, ZonedDateTime}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.time.format.DateTimeFormatter
import scala.Console.println
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.tools.nsc.tasty.SafeEq
import scala.util.{Failure, Success}
import com.ebctech.web.control.JsonFormat._



object LkqHandlers extends DefaultJsonProtocol with SprayJsonSupport {
  private final val logger = LoggerFactory.getLogger(this.getClass).asInstanceOf[Logger]

private val dataBaseService = new DataBaseService()


  def trackOrder(request : TrackingId)(implicit system: ActorSystem): Future[TrackingResponse] ={


    val validIdFuture = dataBaseService.getValidId(request.trackingNum)

    validIdFuture.onComplete {
      case Success(value) =>

        logger.info(s"value:  ${value.toString}")
        Future.successful(1)

      case Failure(exception) =>
        logger.error(s"${exception.getMessage}")
        Future.successful(0)
    }




    Http().singleRequest(HttpRequest(HttpMethods.GET, uri = s"url/${request.trackingNum}").withHeaders(headers.RawHeader("as-api-key", "asat_79a98ce5e61c46c79b523a7844d5dd33"))).flatMap { response =>


      if (response.status.intValue == 200) {
        Unmarshal(response.entity).to[String].flatMap { res =>
          Future.successful(TrackingResponse(200, res.parseJson, request.trackingNum))
        }

      } else {
        response.discardEntityBytes()
        Future.successful(TrackingResponse(400, JsString(response.toString), request.trackingNum))
      }
    }
  }



  }