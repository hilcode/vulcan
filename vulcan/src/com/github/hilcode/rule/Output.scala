package com.github.hilcode.rule

import com.github.hilcode.glob.Globber

final case class Output private (
        globbers: Vector[Globber],
      ) {

    val id: String = s"OUT:${globbers.map(_.id).mkString("|")}"

}

object Output {

    private def apply(
            globbers: Vector[Globber],
          ): Output = {
        new Output(globbers)
    }

    def apply(
            globber: Globber,
            globbers: Globber*,
          ): Output = {
        Output(globbers.toVector.prepended(globber))
    }

}
