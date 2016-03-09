package com.dyrkin.env.dependency

import java.io.File

/**
  * @author eugene zadyra
  */
package object plugin {

  implicit class Directory(file: File) {
    def find(extensions: String*) = {
      def find(f: File): Array[File] = {
        val these = f.listFiles
        these ++ these.filter(_.isDirectory).flatMap(find)
      }
      find(file).filter(f => extensions.exists(ext => f.getName.endsWith(s".$ext")))
    }

    def empty(): Unit = {
      def empty(f: File): Unit = {
        val these = f.listFiles
        these.filterNot(_.isDirectory).foreach(_.delete())
        these.filter(_.isDirectory).foreach(empty)
      }

      if (file.exists()) {
        empty(file)
        file.delete()
      }
    }
  }

}
