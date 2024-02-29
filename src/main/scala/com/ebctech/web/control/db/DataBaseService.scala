package com.ebctech.web.control.db

import akka.actor.ActorSystem
import com.ebctech.web.control.actor.{LkqRecordEntity, LkqServiceQuery, Tracking}
import com.ebctech.web.control.service.{DbProvider, LkqHandlers}
import org.slf4j.{Logger, LoggerFactory}
import com.ebctech.web.control.db.entity.LkqRecordTable

import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class DataBaseService   {

  private final val  logger = LoggerFactory.getLogger(this.getClass).asInstanceOf[Logger]

  private val db = DbProvider.getInstance()


  def getValidId(request: String): Future[Int] = {
    val query = LkqServiceQuery
      .filter(_.tracking_number === request)
      .distinct
      .result

    val resultFuture: Future[Seq[LkqRecordTable#TableElementType]]  = db.run(query)

    resultFuture.map { value =>
      if (value.nonEmpty) {
        logger.info(s"Valid Tracking Number: --  $request ")
        1
      } else {
        logger.info(s"Invalid Tracking Number: $request  ")
        0
      }
    }.recover {
      case exception =>
        logger.error(s"Error while querying the database: ${exception.getMessage}")
        0
    }
  }






}




