package com.avast.datadog4s.statsd.metric

import cats.effect.Sync
import com.avast.datadog4s.api.Tag
import com.avast.datadog4s.api.metric.Gauge
import com.timgroup.statsd.StatsDClient
import scala.collection.immutable.Seq

class GaugeDoubleImpl[F[_]: Sync](
  statsDClient: StatsDClient,
  aspect: String,
  sampleRate: Double,
  defaultTags: Seq[Tag]
) extends Gauge[F, Double] {
  private val F = Sync[F]

  override def set(value: Double, tags: Tag*): F[Unit] =
    F.delay {
      val finalTags = tags ++ defaultTags
      statsDClient.recordGaugeValue(aspect, value, sampleRate, finalTags *)
    }
}
