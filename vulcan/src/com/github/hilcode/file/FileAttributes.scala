package com.github.hilcode.file

import com.github.hilcode.misc.Timestamp
import upickle.default
import upickle.default.ReadWriter
import com.github.hilcode.file.FileEntryWithAttributes

final case class FileAttributes(
        timestamp: Timestamp,
        size: FileSize,
        key: FileKey,
        hash: FileHash,
      )

object FileAttributes {

    implicit val ReadWriterFileAttributes: ReadWriter[FileAttributes] = default.macroRW[FileAttributes]

    def apply(
            fileEntryWithAttributes: FileEntryWithAttributes,
          ): FileAttributes = {
        new FileAttributes(
                fileEntryWithAttributes.timestamp,
                fileEntryWithAttributes.size,
                fileEntryWithAttributes.key,
                fileEntryWithAttributes.hash,
        )
    }

}
