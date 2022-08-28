package com.github.hilcode

import com.github.hilcode.file.FileEntry
import com.github.hilcode.glob.Glob
import com.github.hilcode.glob.Globber
import com.github.hilcode.rule.Input
import com.github.hilcode.rule.Output
import com.github.hilcode.rule.Rule
import com.github.hilcode.rule.Settings
import com.github.hilcode.misc.Separator

object Main {

    def main(
            args: Array[String],
          ): Unit = {
        val projectDirectory: FileEntry = FileEntry.currentWorkingDirectory()
        println(projectDirectory)
        val vulcanDirectory: FileEntry = projectDirectory.resolve(".vulcan")
        val cacheDirectory: FileEntry = vulcanDirectory.resolve("cache")
        cacheDirectory.createDirectories()
        implicit val separator: Separator = Separator("|")
        val sourceGlobber: Globber = Globber(
                Glob.exclude.directory.matching(".vulcan"),
                Glob.exclude.directory.matching("out"),
                Glob.include.file.matching("**/*.java"),
        )
        val (sourceGlobberId, sourceGlobberHash) = sourceGlobber.glob(projectDirectory, cacheDirectory)
        println(s"$sourceGlobberId: $sourceGlobberHash")
        val targetGlobber: Globber = Globber(
                Glob.include.directory.matching(".vulcan"),
                Glob.exclude.directory.notMatching(".vulcan/**/classes/**"),
                Glob.include.file.matching(".vulcan/**/*.class"),
        )
        val (targetGlobberId, targetGlobberHash) = targetGlobber.glob(projectDirectory, cacheDirectory)
        println(s"$targetGlobberId: $targetGlobberHash")
        val rule: Rule = Rule(Input(sourceGlobber), Settings(), Output(targetGlobber))
        println(rule.id)
        println("Done")
    }

}
