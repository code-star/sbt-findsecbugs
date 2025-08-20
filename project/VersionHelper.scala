object VersionHelper {
  def myVersion(input: Option[sbtdynver.GitDescribeOutput], isSnapshot: Boolean): String = {
    input match {
      case Some(v) =>
        val version = v.ref.dropPrefix
        if (isSnapshot) {
          s"$version-SNAPSHOT"
        } else {
          version
        }
      case _ => "0.0.0-SNAPSHOT"
    }
  }
}
