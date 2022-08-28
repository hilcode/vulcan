package com.github.hilcode.file

import upickle.default
import upickle.default.ReadWriter

final case class FileHash(
        value: String,
      ) extends AnyVal {

    override def toString: String = {
        value
    }

}

object FileHash {

    implicit val ReadWriterFileHash: ReadWriter[FileHash] = default.readwriter[String].bimap[FileHash](
            fileHash => fileHash.value,
            value => FileHash(value),
    )

    def apply(
            value: String,
          ): FileHash = {
        new FileHash(value)
    }

}
