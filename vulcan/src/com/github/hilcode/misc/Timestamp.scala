package com.github.hilcode.misc

import upickle.default
import upickle.default.ReadWriter
import java.time.Instant

final case class Timestamp(
        value: Instant,
      ) extends AnyVal {

    override def toString: String = {
        value.toString
    }

}

object Timestamp {

    implicit val ReadWriterTimestamp: ReadWriter[Timestamp] = default.readwriter[String].bimap[Timestamp](
            timestamp => timestamp.value.toString,
            value => Timestamp(Instant.parse(value)),
    )

    def apply(
            value: String,
          ): Timestamp = {
        new Timestamp(Instant.parse(value))
    }

}
