package com.avast.datadog4s.extension

import com.avast.datadog4s.api.tag.TagValue
import org.http4s.{ Method, Status }
import org.http4s.metrics.TerminationType

package object http4s {
  implicit val methodTagValue: TagValue[Method] = TagValue[String].contramap(_.name)

  implicit val terminationTypeTagValue: TagValue[TerminationType] = TagValue[String].contramap {
    case TerminationType.Abnormal => "abnormal"
    case TerminationType.Error    => "error"
    case TerminationType.Timeout  => "timeout"
  }

  implicit val statusTagValue: TagValue[Status] = TagValue[String].contramap(_.code.toString)
}
