package com.ziverge.kubectlplugin.commands

import com.coralogix.zio.k8s.client.K8sFailure
import com.coralogix.zio.k8s.client.v1.pods
import com.coralogix.zio.k8s.client.v1.pods.Pods
import com.coralogix.zio.k8s.model.core.v1.Pod
import com.ziverge.kubectlplugin.models.PodInfo
import com.ziverge.kubectlplugin.{ reports, Format }
import zio.console.Console
import zio.logging.{ log, Logging }
import zio.{ console, IO, ZIO }

object List {
  def run(format: Format): ZIO[Pods with Console with Logging, Nothing, Unit] =
    for {
      _ <- log.debug("Executing the list command")
      _ <- pods
             .getAll(namespace = None)
             .mapM(toModel)
             .run(reports.sink(format))
             .catchAll { k8sFailure =>
               console.putStrLnErr(s"Failed to get the list of pods: $k8sFailure")
             }
    } yield ()

  /** Process a K8s Pod object to create [[PodInfo]]
    *
    * As the fields we are looking for are optional in the schema, we use
    * the 'getXXX' accessors to fail in case of absence of the field.
    */
  private def toModel(pod: Pod): IO[K8sFailure, PodInfo] =
    for {
      metadata  <- pod.getMetadata
      name      <- pod.getName
      namespace <- metadata.getNamespace
      status    <- pod.getStatus
      phase     <- status.getPhase
      message    = status.message.getOrElse("")
    } yield PodInfo(name, namespace, phase, message)
}
