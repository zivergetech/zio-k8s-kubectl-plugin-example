package com.ziverge.kubectlplugin

import cats.free.Free
import io.github.vigoo.clipp._
import io.github.vigoo.clipp.syntax.{ command, _ }

final case class Parameters(verbose: Boolean, command: Command)
object Parameters {
  val spec: Free[Parameter, Parameters] =
    for {
      _           <- metadata("kubectl lp")
      verbose     <- flag("Verbose logging", 'v', "verbose")
      commandName <- command("version", "list")
      command     <- commandName match {
                       case "version" => pure(Command.Version)
                       case "list"    =>
                         for {
                           specifiedFormat <- optional {
                                                namedParameter[Format](
                                                  "Output format",
                                                  "default|json|yaml",
                                                  'o',
                                                  "output"
                                                )
                                              }
                           format           = specifiedFormat.getOrElse(Format.Default)
                         } yield Command.ListPods(format)
                     }
    } yield Parameters(verbose, command)
}

sealed trait Format
object Format {
  case object Default extends Format
  case object Json extends Format
  case object Yaml extends Format

  implicit val parameterParser: ParameterParser[Format] = new ParameterParser[Format] {
    override def parse(value: String): Either[String, Format] =
      value.toLowerCase match {
        case "default" => Right(Format.Default)
        case "json"    => Right(Format.Json)
        case "yaml"    => Right(Format.Yaml)
        case _         => Left(s"Invalid output format '$value', use 'default', 'json' or 'yaml'")
      }

    override def example: Format = Format.Default
  }
}

sealed trait Command
object Command {
  final case class ListPods(format: Format) extends Command
  case object Version extends Command
}
