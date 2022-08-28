import mill.scalalib.ScalaModule
import mill.scalalib.DepSyntax
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalalib.scalafmt.ScalafmtWorkerModule

trait Project extends ScalaModule with ScalafmtModule {

    val SCALA_VERSION = "2.13.8"

    def scalaVersion = SCALA_VERSION

    def compile = T {
        ScalafmtWorkerModule
            .worker()
            .reformat(
                    filesToFormat(sources()),
                    scalafmtConfig().head,
            )
        super.compile()
    }

    override def scalacOptions = Seq(
            "-Wconf:src=out/.*:silent",
            "-encoding", "utf8",
            "-deprecation",
            "-explaintypes",
            "-feature",
            "-unchecked",
            "-Xfatal-warnings",
            s"-Xmigration:$SCALA_VERSION",
            "-Xlint",
            "-Xlint:adapted-args",
            "-Xlint:constant",
            "-Xlint:delayedinit-select",
            "-Xlint:doc-detached",
            "-Xlint:inaccessible",
            "-Xlint:infer-any",
            "-Xlint:missing-interpolator",
            "-Xlint:nullary-unit",
            "-Xlint:option-implicit",
            "-Xlint:package-object-classes",
            "-Xlint:poly-implicit-overload",
            "-Xlint:private-shadow",
            "-Xlint:stars-align",
            "-Xlint:type-parameter-shadow",
            "-Ywarn-dead-code",
            "-Ywarn-extra-implicit",
            "-Ywarn-numeric-widen",
            "-Wunused",
            "-Ywarn-unused:implicits",
            "-Ywarn-unused:imports",
            "-Ywarn-unused:locals",
            "-Ywarn-unused:params",
            "-Ywarn-unused:patvars",
            "-Ywarn-unused:privates",
            "-Ywarn-value-discard",
        )

}

object vulcan extends Project {

    def mainClass = Some("com.github.hilcode.Main")

    def compileIvyDeps = Agg(
        ivy"commons-codec:commons-codec:1.15",
        ivy"dev.zio::zio:2.0.0",
        ivy"com.lihaoyi::upickle:2.0.0",
    )

    def ivyDeps = compileIvyDeps

}

def millw() = T.command {
    val target = mill.modules.Util.download("https://raw.githubusercontent.com/lefou/millw/main/millw")
    val millw = build.millSourcePath / "mill"
    os.copy.over(target.path, millw)
    os.perms.set(millw, os.perms(millw) + java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE)
    target
}
