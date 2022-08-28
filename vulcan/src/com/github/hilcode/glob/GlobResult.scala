package com.github.hilcode.glob

sealed abstract class GlobResult extends Product with Serializable

object GlobResult {

    case object INCLUDE extends GlobResult

    case object EXCLUDE extends GlobResult

}
