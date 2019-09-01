package com.avast.datadog4s.api.tag

import com.avast.datadog4s.api.Tag

trait Tagger[A] {
  def tag(a: A): Tag
}

object Tagger {
  def make[A: TagValue](name: String): Tagger[A] = new Tagger[A] {

    private[this] val tagValue = TagValue[A]

    override def tag(a: A): Tag = Tag.of(name, tagValue.convert(a))

  }
}
