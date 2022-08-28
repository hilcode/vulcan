import mill.scalalib.ScalaModule
import mill.scalalib.DepSyntax

object hephaestus extends ScalaModule {

    def scalaVersion = "3.1.3"

    def mainClass = Some("com.github.hilcode.Main")

    def compileIvyDeps = Agg(
        ivy"dev.zio::zio:2.0.0",
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
