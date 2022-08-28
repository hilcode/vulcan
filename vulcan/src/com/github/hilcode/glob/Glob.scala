package com.github.hilcode.glob

import com.github.hilcode.file.FileEntry

import java.nio.file.FileSystems
import java.nio.file.PathMatcher
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.Path

sealed abstract class FilterType extends Product with Serializable

object FilterType {

    case object MATCHING extends FilterType

    case object NOT_MATCHING extends FilterType

}

sealed abstract class Glob extends Product with Serializable {

    val id: GlobId

    def check(
            fileEntry: FileEntry,
            fileAttributes: BasicFileAttributes,
          ): Option[GlobResult]

}

object Glob {

    def invert(
            pathMatcher: PathMatcher,
          ): PathMatcher = {
        new InversePathMatcher(pathMatcher)
    }

    final class InversePathMatcher(
            delegate: PathMatcher,
          ) extends PathMatcher {

        override def matches(
                path: Path,
              ): Boolean = {
            !delegate.matches(path)
        }

    }

    object include {

        object directory {

            def matching(
                    globPattern: String,
                  ): Glob = {
                IncludeDirectory(globPattern, FileSystems.getDefault().getPathMatcher(s"glob:$globPattern"))
            }

            def notMatching(
                    globPattern: String,
                  ): Glob = {
                IncludeDirectory(globPattern, invert(FileSystems.getDefault().getPathMatcher(s"glob:$globPattern")))
            }

        }

        object file {

            def matching(
                    globPattern: String,
                  ): Glob = {
                new IncludeFile(globPattern, FileSystems.getDefault().getPathMatcher(s"glob:$globPattern"))
            }

            def notMatching(
                    globPattern: String,
                  ): Glob = {
                new IncludeFile(globPattern, invert(FileSystems.getDefault().getPathMatcher(s"glob:$globPattern")))
            }

        }

    }

    object exclude {

        object directory {

            def matching(
                    globPattern: String,
                  ): Glob = {
                ExcludeDirectory(globPattern, FileSystems.getDefault().getPathMatcher(s"glob:$globPattern"))
            }

            def notMatching(
                    globPattern: String,
                  ): Glob = {
                ExcludeDirectory(globPattern, invert(FileSystems.getDefault().getPathMatcher(s"glob:$globPattern")))
            }

        }

        object file {

            def matching(
                    globPattern: String,
                  ): Glob = {
                new ExcludeFile(globPattern, FileSystems.getDefault().getPathMatcher(s"glob:$globPattern"))
            }

            def notMatching(
                    globPattern: String,
                  ): Glob = {
                new ExcludeFile(globPattern, invert(FileSystems.getDefault().getPathMatcher(s"glob:$globPattern")))
            }

        }

    }

    final private case class IncludeDirectory private[Glob] (
            globPattern: String,
            pathMatcher: PathMatcher,
          ) extends Glob {

        override def toString: String = {
            globPattern
        }

        override val id = GlobId.include.directory(globPattern)

        override def check(
                fileEntry: FileEntry,
                fileAttributes: BasicFileAttributes,
              ): Option[GlobResult] = {
            if (fileAttributes.isDirectory() && pathMatcher.matches(fileEntry.value)) {
                Some(GlobResult.INCLUDE)
            } else {
                None
            }
        }

    }

    final private case class ExcludeDirectory private[Glob] (
            globPattern: String,
            pathMatcher: PathMatcher,
          ) extends Glob {

        override def toString: String = {
            globPattern
        }

        override val id = GlobId.exclude.directory(globPattern)

        override def check(
                fileEntry: FileEntry,
                fileAttributes: BasicFileAttributes,
              ): Option[GlobResult] = {
            if (fileAttributes.isDirectory() && pathMatcher.matches(fileEntry.value)) {
                Some(GlobResult.EXCLUDE)
            } else {
                None
            }
        }

    }

    final private case class IncludeFile private[Glob] (
            globPattern: String,
            pathMatcher: PathMatcher,
          ) extends Glob {

        override def toString: String = {
            globPattern
        }

        override val id = GlobId.include.file(globPattern)

        override def check(
                fileEntry: FileEntry,
                fileAttributes: BasicFileAttributes,
              ): Option[GlobResult] = {
            if (fileAttributes.isRegularFile() && pathMatcher.matches(fileEntry.value)) {
                Some(GlobResult.INCLUDE)
            } else {
                None
            }
        }

    }

    final private case class ExcludeFile private[Glob] (
            globPattern: String,
            pathMatcher: PathMatcher,
          ) extends Glob {

        override def toString: String = {
            globPattern
        }

        override val id = GlobId.exclude.file(globPattern)

        override def check(
                fileEntry: FileEntry,
                fileAttributes: BasicFileAttributes,
              ): Option[GlobResult] = {
            if (fileAttributes.isRegularFile() && pathMatcher.matches(fileEntry.value)) {
                Some(GlobResult.EXCLUDE)
            } else {
                None
            }
        }

    }

}
