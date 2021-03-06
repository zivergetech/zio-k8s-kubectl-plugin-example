package com.ziverge.kubectlplugin.commands

import com.ziverge.kubectlplugin.BuildInfo
import zio.console.Console
import zio.{ZIO, console}
import zio.logging.{Logging, log}

object Version {
  def run(): ZIO[Console with Logging, Nothing, Unit] =
    for {
      _ <- log.debug("Executing the version command")
      _ <- console.putStrLn(BuildInfo.version)
    } yield ()
}
