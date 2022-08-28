package com.github.hilcode.file

import upickle.default
import upickle.default.ReadWriter

import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

final case class FileEntry(
        value: Path,
      ) extends AnyVal {

    def exists(): Boolean = {
        Files.exists(value)
    }

    def resolve(
            path: String,
          ): FileEntry = {
        FileEntry(value.resolve(path))
    }

    def resolve(
            path: FileEntry,
          ): FileEntry = {
        FileEntry(value.resolve(path.value))
    }

    def createDirectories(): FileEntry = {
        Files.createDirectories(value)
        this
    }

    def relativize(
            path: Path,
          ): FileEntry = {
        FileEntry(value.relativize(path))
    }

    def readContent(): Array[Byte] = {
        Files.readAllBytes(value)
    }

    def readString(): String = {
        Files.readString(value)
    }

    def writeString(
            text: String,
          ): FileEntry = {
        Files.writeString(value, text)
        this
    }

    def walkFileTree(
            visitor: FileVisitor[Path],
          ): Unit = {
        val _ = Files.walkFileTree(value, visitor)
    }

    override def toString: String = {
        value.toString
    }

}

object FileEntry {

    implicit val OrderingFileEntry: Ordering[FileEntry] = new Ordering[FileEntry]() {

        override def compare(
                left: FileEntry,
                right: FileEntry,
              ): Int = {
            left.value.compareTo(right.value)
        }

    }

    implicit val ReadWriterFileEntry: ReadWriter[FileEntry] = default.readwriter[String].bimap[FileEntry](
            fileEntry => fileEntry.value.toString,
            value => FileEntry(Path.of(value)),
    )

    def apply(
            value: String,
          ): FileEntry = {
        new FileEntry(Path.of(value))
    }

    def currentWorkingDirectory(): FileEntry = {
        FileEntry(Paths.get(".").toAbsolutePath().normalize())
    }

}
