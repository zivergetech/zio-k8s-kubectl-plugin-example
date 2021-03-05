package com.ziverge.kubectlplugin.models

import com.ziverge.kubectlplugin.reports.TableReport.Tabular
import de.vandermeer.asciitable.{ AsciiTable, CWC_LongestWordMin }
import de.vandermeer.asciithemes.a7.A7_Grids
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import com.ziverge.kubectlplugin.reports.TableReport.Tabular
import zio.{ UIO, ZManaged }

/** Data structure gathered by the 'list' command containing information about pods
  */
case class PodInfo(name: String, namespace: String, status: String, message: String)

object PodInfo {

  /** JSON / YAML encoder, auto derived */
  implicit val encoder: Encoder[PodInfo] = deriveEncoder

  /** Implementation of [[Tabular]] specifies how to render [[PodInfo]] with 'asciitable' */
  implicit val tabular: Tabular[PodInfo] = new Tabular[PodInfo] {
    def createTableRenderer(): ZManaged[Any, Nothing, AsciiTable] =
      UIO {
        val table = new AsciiTable()

        table.addRule()
        table.addRow("NAMESPACE", "NAME", "STATUS", "MESSAGE")
        table.addRule()

        table
      }.toManaged_

    def renderTable(table: AsciiTable): UIO[String] =
      UIO {
        table.addRule()
        table.setPaddingLeftRight(1)
        table.setTextAlignment(TextAlignment.LEFT)

        table.getRenderer.setCWC(new CWC_LongestWordMin(3))
        table.getContext.setGrid(A7_Grids.minusBarPlus())
        table.render()
      }

    def addRow(table: AsciiTable)(pod: PodInfo): UIO[Unit] =
      UIO(table.addRow(pod.namespace, pod.name, pod.status, pod.message))
  }
}
