package com.avast.cloud.metrics.datadog.noop.metric

import java.time.Duration

import cats.Applicative
import com.avast.cloud.metrics.datadog.api.Tag
import com.avast.cloud.metrics.datadog.api.metric.Timer

class NoopTimer[F[_]: Applicative] extends Timer[F] {
  override def time[A](f: F[A], tags: Tag*): F[A] = f

  override def record(duration: Duration, tags: Tag*): F[Unit] = Applicative[F].unit
}
