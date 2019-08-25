package com.avast.datadog4s.api.metric

import com.avast.datadog4s.api.Tag

trait Histogram[F[_], N] {
  def record(value: N, tags: Tag*): F[Unit]
}
