package com.ziverge.kubectlplugin

import com.coralogix.zio.k8s.client.config.httpclient.k8sDefault
import com.coralogix.zio.k8s.client.v1.pods.Pods
import io.github.vigoo.clipp.ParserFailure
import io.github.vigoo.clipp.zioapi.config
import io.github.vigoo.clipp.zioapi.config.ClippConfig
import zio.clock.Clock
import zio.console.Console
import zio.logging.slf4j.bridge.initializeSlf4jBridge
import zio.logging.{ LogLevel, Logging }
import zio.{ App, ExitCode, URIO, ZEnv, ZIO, ZLayer }

object Main extends App {

  /** Entry point */
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    // Parsing the command line arguments.
    // In case of failure it will print usage information automatically

    val clippConfig = config.fromArgsWithUsageInfo(args, Parameters.spec)
    runWithParameters()
      .provideCustomLayer(clippConfig)
      .catchAll { _: ParserFailure => ZIO.succeed(ExitCode.failure) }
  }

  /** Entry point with parsed command line arguments */
  private def runWithParameters(): ZIO[ZEnv with ClippConfig[Parameters], Nothing, ExitCode] =
    for {
      // Setting up command line parameter dependent ZIO environment
      parameters <- config.parameters[Parameters]
      logging     = configuredLogging(parameters.verbose)

      k8s = k8sDefault
      env = logging ++ (k8s >>> Pods.live)

      // Run the selected command within the created environment
      result <- runCommand(parameters.command)
                  .provideCustomLayer(env)
                  .exitCode
    } yield result

  /** Creates verbosity option dependent logging layer and initializes the Slf4j bridge
    * so the K8s requests will be logged
    */
  private def configuredLogging(verbose: Boolean): ZLayer[Console with Clock, Nothing, Logging] = {
    val logLevel = if (verbose) LogLevel.Trace else LogLevel.Info
    Logging.consoleErr(logLevel) >>> initializeSlf4jBridge
  }

  /** Run one of the supported commands */
  private def runCommand(command: Command): ZIO[Console with Logging with Pods, Nothing, Unit] =
    command match {
      case Command.ListPods(format) =>
        commands.List.run(format)

      case Command.Version =>
        commands.Version.run()
    }
}
