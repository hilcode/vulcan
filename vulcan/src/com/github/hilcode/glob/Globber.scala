package com.github.hilcode.glob

import com.github.hilcode.file.FileAttributes
import com.github.hilcode.file.FileEntry
import com.github.hilcode.file.FileEntryWithAttributes
import com.github.hilcode.misc.Separator
import org.apache.commons.codec.digest.MurmurHash3

import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

final case class Globber(
        globs: Vector[Glob],
      ) {

    val id: String = globs.map(_.id.value).mkString("-")

    def check(
            fileEntry: FileEntry,
            fileAttributes: BasicFileAttributes,
          ): Option[GlobResult] = {
        if (fileEntry.value == Path.of("")) {
            Some(GlobResult.INCLUDE)
        } else {
            globs.collectFirst(glob => {
                glob.check(fileEntry, fileAttributes) match {
                    case Some(globResult) =>
                        println(s"[$glob] $globResult: $fileEntry")
                        globResult
                }
            })
        }
    }

    def glob(
            projectDirectory: FileEntry,
            cacheDirectory: FileEntry,
          )(
            implicit separator: Separator,
          ): (String, String) = {
        val globberFile: FileEntry = cacheDirectory.resolve(id)
        val cacheDatabase: Map[FileEntry, FileAttributes] = {
            if (globberFile.exists()) {
                upickle.default.read[Vector[FileEntryWithAttributes]](globberFile.readString())
                    .map(fileEntryWithAttributes => (fileEntryWithAttributes.fileEntry, FileAttributes(fileEntryWithAttributes)))
                    .toMap
            } else {
                Map.empty
            }
        }
        val visitor = new GlobVisitor(projectDirectory, cacheDatabase, this)
        projectDirectory.walkFileTree(visitor)
        val fileEntries: Vector[FileEntry] = visitor.map.keySet.toArray.sortInPlace().toVector
        val pathDetails: Vector[FileEntryWithAttributes] = fileEntries.map(path => FileEntryWithAttributes(path, visitor.map(path)))
        globberFile.writeString(upickle.default.writeJs(pathDetails).render(indent = 4))
        val parts: Array[Long] = MurmurHash3.hash128(pathDetails.map(_.toHash).mkString("\n").getBytes())
        (id, f"${parts(0).toHexString}%16s${parts(1).toHexString}%16s".replace(' ', '0'))
    }

}

object Globber {

    def apply(
            globs: Glob*,
          ): Globber = {
        new Globber(globs.toVector)
    }

}
