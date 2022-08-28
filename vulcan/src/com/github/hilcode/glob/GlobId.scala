package com.github.hilcode.glob

import org.apache.commons.codec.binary.Base64

final case class GlobId private (
        value: String,
      ) extends AnyVal

object GlobId {

    object include {

        def directory(
                globPattern: String,
              ): GlobId = {
            GlobId(INCLUDE, DIRECTORY, globPattern)
        }

        def file(
                globPattern: String,
              ): GlobId = {
            GlobId(INCLUDE, FILE, globPattern)
        }

    }

    object exclude {

        def directory(
                globPattern: String,
              ): GlobId = {
            GlobId(EXCLUDE, DIRECTORY, globPattern)
        }

        def file(
                globPattern: String,
              ): GlobId = {
            GlobId(EXCLUDE, FILE, globPattern)
        }

    }

    private val INCLUDE = true
    private val EXCLUDE = false
    private val DIRECTORY = true
    private val FILE = false

    private def apply(
            inclusion: Boolean,
            directory: Boolean,
            globPattern: String,
          ): GlobId = {
        val inclusionType = if (inclusion) "+" else "-"
        val fileType = if (directory) "d" else "f"
        new GlobId(Base64.encodeBase64URLSafeString(s"$inclusionType$fileType[$globPattern]".getBytes()))
    }

}
