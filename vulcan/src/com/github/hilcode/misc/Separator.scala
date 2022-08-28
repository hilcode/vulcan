package com.github.hilcode.misc

final case class Separator(
        value: String,
      ) extends AnyVal {

    def join(
            values: String*,
          ): String = {
        values.mkString(value)
    }

}
