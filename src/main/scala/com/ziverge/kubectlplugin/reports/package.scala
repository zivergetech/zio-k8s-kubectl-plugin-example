package com.ziverge.kubectlplugin

import com.ziverge.kubectlplugin.reports.TableReport.Tabular
import io.circe.Encoder
import com.ziverge.kubectlplugin.reports.TableReport.Tabular
import zio.console.Console
import zio.stream.ZSink

package object reports {

  /** Creates a ZIO Sink that is responsible for consuming the stream of T items and
    * rendering them in the specified format.
    */
  def sink[T: Encoder: Tabular](format: Format): ZSink[Console, Nothing, T, T, Unit] =
    format match {
      case Format.Default =>
        TableReport.sink[T]
      case Format.Json    =>
        JsonReport.sink[T]
      case Format.Yaml    =>
        YamlReport.sink[T]
    }
}
