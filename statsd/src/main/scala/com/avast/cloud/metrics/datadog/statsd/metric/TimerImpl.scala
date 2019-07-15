package com.avast.cloud.metrics.datadog.statsd.metric

import java.time.Duration
import java.util.concurrent.TimeUnit

import cats.effect.{ Clock, Sync }
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.avast.cloud.metrics.datadog.api.Tag
import com.avast.cloud.metrics.datadog.api.metric.Timer
import com.timgroup.statsd.StatsDClient
import scala.collection.immutable.Seq

class TimerImpl[F[_]: Sync](
  clock: Clock[F],
  statsDClient: StatsDClient,
  aspect: String,
  sampleRate: Double,
  defaultTags: Seq[Tag]
) extends Timer[F] {

  private[this] val F                       = Sync[F]
  private[this] val failedTag: Tag          = Tag.of("success", "false")
  private[this] val succeededTag: Tag       = Tag.of("success", "true")
  private[this] val exceptionTagKey: String = "exception"

  override def time[A](value: F[A], tags: Tag*): F[A] =
    for {
      start <- clock.monotonic(TimeUnit.NANOSECONDS)
      a     <- F.recoverWith(value)(measureFailed(start))
      stop  <- clock.monotonic(TimeUnit.NANOSECONDS)
      _     <- record(Duration.ofNanos(stop - start), (tags :+ succeededTag): _*)
    } yield {
      a
    }

  private def measureFailed[A](startTime: Long, tags: Tag*): PartialFunction[Throwable, F[A]] = {
    case thr =>
      val finalTags = tags :+ Tag.of(exceptionTagKey, thr.getClass.getName) :+ failedTag
      val computation = for {
        stop <- clock.monotonic(TimeUnit.NANOSECONDS)
        _    <- record(Duration.ofNanos(stop - startTime), finalTags: _*)
      } yield {
        Unit
      }
      computation >> F.raiseError(thr)
  }

  override def record(duration: Duration, tags: Tag*): F[Unit] = F.delay {
    statsDClient.recordExecutionTime(aspect, duration.toMillis, sampleRate, (tags ++ defaultTags): _*)
  }
}
