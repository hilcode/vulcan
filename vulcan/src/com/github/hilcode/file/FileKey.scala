package com.github.hilcode.file

import upickle.default
import upickle.default.ReadWriter

final case class FileKey(
        value: String,
      ) extends AnyVal {

    override def toString: String = {
        value
    }

}

object FileKey {

    implicit val ReadWriterFileKey: ReadWriter[FileKey] = default.readwriter[String].bimap[FileKey](
            fileKey => fileKey.value,
            value => FileKey(value),
    )

    def apply(
            value: String,
          ): FileKey = {
        new FileKey(value)
    }

}
