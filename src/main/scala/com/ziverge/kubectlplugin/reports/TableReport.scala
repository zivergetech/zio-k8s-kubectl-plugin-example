package com.ziverge.kubectlplugin.reports

import de.vandermeer.asciitable.AsciiTable
import zio.console.Console
import zio.stream.ZSink
import zio.{ console, UIO, ZManaged }

object TableReport {

  /** Type class defining how to render a given T type with the Java library 'asciitable'
    */
  trait Tabular[T] {

    /** Initializes a table by setting properties and adding header rows
      */
    def createTableRenderer(): ZManaged[Any, Nothing, AsciiTable]

    /** Adds a single item of type T to the table created with [[createTableRenderer()]]
      */
    def addRow(table: AsciiTable)(item: T): UIO[Unit]

    /** Adds the table's footer and renders it to a string
      */
    def renderTable(table: AsciiTable): UIO[String]
  }

  def sink[T](implicit tabular: Tabular[T]): ZSink[Console, Nothing, T, T, Unit] =
    ZSink.managed[Console, Nothing, T, AsciiTable, T, Unit](tabular.createTableRenderer()) {
      table => // initialize the table
        ZSink.foreach(tabular.addRow(table)) <* // add each row
          printResultTable[T](table) // print the result
    }

  private def printResultTable[T](
    table: AsciiTable
  )(implicit tabular: Tabular[T]): ZSink[Console, Nothing, T, T, Unit] =
    ZSink.fromEffect {
      tabular
        .renderTable(table)
        .flatMap(str => console.putStrLn(str))
    }
}
