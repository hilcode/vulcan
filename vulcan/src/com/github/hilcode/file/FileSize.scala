package com.github.hilcode.file

import upickle.default
import upickle.default.ReadWriter

final case class FileSize(
        value: Long,
      ) extends AnyVal {

    override def toString: String = {
        value.toString
    }

}

object FileSize {

    implicit val ReadWriterFileSize: ReadWriter[FileSize] = default.readwriter[Long].bimap[FileSize](
            fileSize => fileSize.value,
            value => FileSize(value),
    )

    def apply(
            value: String,
          ): FileSize = {
        FileSize(value.toLong)
    }

}
