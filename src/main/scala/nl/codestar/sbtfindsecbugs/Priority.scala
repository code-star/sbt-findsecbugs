package nl.codestar.sbtfindsecbugs

object Priority {

  sealed case class Priority(name: String)

  /**
    * Experimental priority for bug instances.
    */
  val Experimental: Priority = Priority("experimental")

  /**
    * Low priority for bug instances.
    */
  val Low: Priority = Priority("low")

  /**
    * Normal priority for bug instances.
    */
  val Normal: Priority = Priority("medium")

  /**
    * High priority for bug instances.
    */
  val High: Priority = Priority("high")
}
