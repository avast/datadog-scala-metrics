package com.avast.datadog4s.extension.http4s

import cats.effect.Sync
import com.avast.datadog4s.api.{ MetricFactory, Tag }
import com.avast.datadog4s.extension.http4s.MetricsOpsBuilder.defaultClassifierTags
import org.http4s.metrics.MetricsOps

object DatadogMetricsOps {
  type ClassifierTags = String => List[Tag]

  @deprecated("Deprecated in favor of DatadogMetricsOps#builder", "0.12")
  def make[F[_]](
    metricFactory: MetricFactory[F],
    classifierTags: ClassifierTags = defaultClassifierTags
  )(implicit
    F: Sync[F]
  ): F[MetricsOps[F]] =
    builder(metricFactory).setClassifierTags(classifierTags).build()

  def builder[F[_]: Sync](metricFactory: MetricFactory[F]): MetricsOpsBuilder[F] =
    new MetricsOpsBuilder[F](metricFactory)

}
