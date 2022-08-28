package com.github.hilcode.rule

import org.apache.commons.codec.binary.Base64

final case class RuleId private (
        value: String,
      ) extends AnyVal

object RuleId {

    private def apply(
            value: String,
          ): RuleId = {
        new RuleId(value)
    }

    def apply(
            rule: Rule,
          ): RuleId = {
        val x1 = Base64.encodeBase64URLSafeString(rule.input.id.getBytes())
        val x2 = Base64.encodeBase64URLSafeString(rule.settings.id.getBytes())
        val x3 = Base64.encodeBase64URLSafeString(rule.output.id.getBytes())
        RuleId(s"$x1-$x2-$x3")
    }

}
