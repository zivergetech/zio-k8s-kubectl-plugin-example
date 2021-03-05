package com.ziverge.kubectlplugin.commands

import zio.console.Console
import zio.{ console, BuildInfo, ZIO }
import zio.logging.{ log, Logging }

object Version {
  def run(): ZIO[Console with Logging, Nothing, Unit] =
    for {
      _ <- log.debug("Executing the version command")
      _ <- console.putStrLn(BuildInfo.version)
    } yield ()
}
