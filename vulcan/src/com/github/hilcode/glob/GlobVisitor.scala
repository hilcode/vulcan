package com.github.hilcode.glob

import com.github.hilcode.file.FileAttributes
import com.github.hilcode.file.FileEntry
import com.github.hilcode.file.FileHash
import com.github.hilcode.file.FileKey
import com.github.hilcode.file.FileSize
import com.github.hilcode.misc.Timestamp

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import scala.collection.mutable.{Map => MutableMap}

final class GlobVisitor(
        rootDirectory: FileEntry,
        cache: Map[FileEntry, FileAttributes],
        globber: Globber,
      ) extends FileVisitor[Path] {

    val map: MutableMap[FileEntry, FileAttributes] = MutableMap.empty

    override def visitFile(
            originalPath: Path,
            basicFileAttributes: BasicFileAttributes,
          ): FileVisitResult = {
        val fileEntry: FileEntry = rootDirectory.relativize(originalPath)
        globber.check(fileEntry, basicFileAttributes) match {
            case Some(GlobResult.INCLUDE) =>
                val timestamp: Timestamp = Timestamp(basicFileAttributes.lastModifiedTime().toInstant)
                val size: FileSize = FileSize(basicFileAttributes.size())
                val key: FileKey = FileKey(basicFileAttributes.fileKey().toString)

                def newFileAttributes(): FileAttributes = {
                    FileAttributes(timestamp, size, key, GlobVisitor.hashPathContents(rootDirectory, fileEntry))
                }

                cache.get(fileEntry) match {
                    case Some(cachedFileAttributes) =>
                        if (
                                cachedFileAttributes.timestamp == timestamp && cachedFileAttributes.size == size && cachedFileAttributes.key == key
                        ) {
                            map.put(fileEntry, cachedFileAttributes)
                        } else {
                            map.put(fileEntry, newFileAttributes())
                        }

                    case None =>
                        map.put(fileEntry, newFileAttributes())
                }

            case Some(_) | None =>
                ()
        }

        FileVisitResult.CONTINUE
    }

    override def preVisitDirectory(
            originalPath: Path,
            basicFileAttributes: BasicFileAttributes,
          ): FileVisitResult = {
        val fileEntry: FileEntry = rootDirectory.relativize(originalPath)
        globber.check(fileEntry, basicFileAttributes) match {
            case Some(GlobResult.EXCLUDE) =>
                FileVisitResult.SKIP_SUBTREE

            case Some(GlobResult.INCLUDE) | None =>
                FileVisitResult.CONTINUE
        }
    }

    override def postVisitDirectory(
            path: Path,
            exception: IOException,
          ): FileVisitResult = {
        FileVisitResult.CONTINUE
    }

    override def visitFileFailed(
            file: Path,
            exception: IOException,
          ): FileVisitResult = {
        FileVisitResult.CONTINUE
    }

}

object GlobVisitor {

    def hashPathContents(
            rootDirectory: FileEntry,
            originalFileEntry: FileEntry,
          ): FileHash = {
        val fileEntry: FileEntry = rootDirectory.resolve(originalFileEntry)
        val content: Array[Byte] = fileEntry.readContent()
        val parts: Array[Long] = org.apache.commons.codec.digest.MurmurHash3.hash128(content)
        FileHash(f"${parts(0).toHexString}%16s${parts(1).toHexString}%16s".replace(' ', '0'))
    }

}
