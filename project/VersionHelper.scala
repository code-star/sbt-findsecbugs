object VersionHelper {
  def myVersion(input: Option[sbtdynver.GitDescribeOutput]): String = {
    def incrementRightmostNumber(s: String): String = {
      val pattern = "(\\d+)(?!.*\\d)".r // match the last (rightmost) group of digits
      val num = pattern.findFirstIn(s)
      num match {
        case Some(n) => pattern.replaceFirstIn(s, (n.toInt + 1).toString)
        case _ => s // if no number found, return the original string
      }
    }
    input match {
      case Some(v) =>
        val version = v.ref.dropPrefix
        // we are a snapshot if the Git commit suffix has a non-zero distance or the Git dirty suffix is not empty
        val isSnapshot = v.commitSuffix.distance > 0 || v.dirtySuffix.value.nonEmpty
        if (isSnapshot) {
          val nextVersion = incrementRightmostNumber(version)
          s"$nextVersion-SNAPSHOT"
        } else {
          version
        }
      case _ => "0.0.0-SNAPSHOT"
    }
  }
}
