package com.github.hilcode.file

import com.github.hilcode.file.FileAttributes
import com.github.hilcode.file.FileEntry
import com.github.hilcode.file.FileHash
import com.github.hilcode.file.FileKey
import com.github.hilcode.file.FileSize
import com.github.hilcode.misc.Timestamp
import upickle.default
import upickle.default.ReadWriter
import scala.util.matching.Regex
import com.github.hilcode.misc.Separator

final case class FileEntryWithAttributes(
        fileEntry: FileEntry,
        timestamp: Timestamp,
        size: FileSize,
        key: FileKey,
        hash: FileHash,
      ) {

    def toHash: String = {
        s"$fileEntry|$hash"
    }

}

object FileEntryWithAttributes {

    implicit def ReadWriterFileEntryWithAttributes(
            implicit separator: Separator,
          ): ReadWriter[FileEntryWithAttributes] = {
        default.readwriter[String].bimap(
                value => s"${value.fileEntry}|${value.timestamp}|${value.size}|${value.key}|${value.hash}",
                value => {
                    val array: Array[String] = value.split(Regex.quote(separator.value))
                    FileEntryWithAttributes(
                            FileEntry(array(0)),
                            Timestamp(array(1)),
                            FileSize(array(2)),
                            FileKey(array(3)),
                            FileHash(array(4)),
                    )
                },
        )
    }

    def apply(
            fileEntry: FileEntry,
            fileAttributes: FileAttributes,
          ): FileEntryWithAttributes = {
        new FileEntryWithAttributes(fileEntry, fileAttributes.timestamp, fileAttributes.size, fileAttributes.key, fileAttributes.hash)
    }

}
