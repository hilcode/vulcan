package com.github.hilcode.rule

import com.github.hilcode.glob.Globber

final case class Input private (
        globbers: Vector[Globber],
      ) {

    val id: String = s"IN:${globbers.map(_.id).mkString("|")}"

}

object Input {

    private def apply(
            globbers: Vector[Globber],
          ): Input = {
        new Input(globbers)
    }

    def apply(
            globber: Globber,
            globbers: Globber*,
          ): Input = {
        Input(globbers.toVector.prepended(globber))
    }

}
