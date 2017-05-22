package com.klout.scoozie

import com.klout.scoozie.dsl.Bundle
import com.klout.scoozie.runner.BundleAppAbs
import com.klout.scoozie.writer.{FileSystemUtils, XmlPostProcessing}
import org.apache.oozie.client.OozieClient

import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}
import scalaxb.CanWriteXML

class BundleApp[B: CanWriteXML,C: CanWriteXML, W: CanWriteXML](override val bundle: Bundle[B, C, W],
                                                               oozieUrl: String,
                                                               override val appPath: String,
                                                               override val fileSystemUtils: FileSystemUtils,
                                                               override val properties: Option[Map[String, String]] = None,
                                                               override val postProcessing: XmlPostProcessing = XmlPostProcessing.Default)
    extends BundleAppAbs[B, C, W] {
  override val oozieClient: OozieClient = new OozieClient(oozieUrl)

  implicit override val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global

  executionResult.onComplete{
    case Success(_) => println(ScoozieConfig.successMessage)
    case Failure(e) => println(s"Application failed with the following error: ${e.getMessage}")
  }

  import scala.concurrent.duration._
  Await.result(executionResult, 5.minutes)
}