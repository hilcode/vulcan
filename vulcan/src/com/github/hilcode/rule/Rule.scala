package com.github.hilcode.rule

final case class Rule(
        input: Input,
        settings: Settings,
        output: Output,
      ) {

    val id: RuleId = RuleId(this)

}
