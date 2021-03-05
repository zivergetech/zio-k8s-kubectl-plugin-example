package com.ziverge.kubectlplugin.reports

import io.circe._
import io.circe.syntax._
import io.circe.yaml.syntax._
import zio.console
import zio.console.Console
import zio.stream.ZSink

object YamlReport {
  def sink[T: Encoder]: ZSink[Console, Nothing, T, T, Unit] =
    ZSink.foreach { (item: T) =>
      console.putStrLn(item.asJson.asYaml.spaces2)
    }
}
