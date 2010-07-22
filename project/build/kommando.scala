import sbt._

class Kommando(projectInfo: ProjectInfo) extends DefaultProject(projectInfo) {

    override def mainClass = Some("kommando.Server")
}
