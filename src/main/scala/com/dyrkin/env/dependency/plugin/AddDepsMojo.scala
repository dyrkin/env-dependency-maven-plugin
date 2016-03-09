package com.dyrkin.env.dependency.plugin

import java.io.File

import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager
import org.apache.maven.artifact.versioning.VersionRange
import org.apache.maven.artifact.{Artifact, DefaultArtifact}
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations._
import org.apache.maven.project.MavenProject

import scala.collection.JavaConversions._
import scala.util.Try

/**
  * @author eugene zadyra
  */
@Mojo(name = "add-deps", defaultPhase = LifecyclePhase.INITIALIZE, requiresDependencyResolution = ResolutionScope.COMPILE)
class AddDepsMojo extends AbstractMojo {

  @Component
  var session: MavenSession = _

  @Component
  var project: MavenProject = _

  @Component
  var artifactHandlerManager: ArtifactHandlerManager = _

  @Parameter(readonly = true, required = true)
  var environmentVariable: String = _


  override def execute(): Unit = {
    val path = Try(sys.env(environmentVariable)) getOrElse sys.error("Please specify parameter <environmentVariable>")
    val libsDir = new File(path)
    val jars = libsDir.find("jar")

    val artifacts = jars.map(jar => createSystemDependency(jar).asInstanceOf[Artifact]).toSet
    project.setResolvedArtifacts(project.getArtifacts ++ artifacts)
  }

  private def createSystemDependency(jar: File) = {
    val handler = artifactHandlerManager.getArtifactHandler("jar")
    val artifact = new DefaultArtifact(jar.getName + "-sys-dep", jar.getName + "-sys-dep", VersionRange.createFromVersion("1.0.0"), Artifact.SCOPE_COMPILE, "jar", null, handler, false)
    artifact.setFile(jar)
    artifact
  }

}
