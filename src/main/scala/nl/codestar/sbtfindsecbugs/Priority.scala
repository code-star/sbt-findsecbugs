package nl.codestar.sbtfindsecbugs

object Priority {

  sealed case class Priority(name: String)

  /**
    * Experimental priority for bug instances.
    */
  val Experimental = Priority("experimental")

  /**
    * Low priority for bug instances.
    */
  val Low = Priority("low")

  /**
    * Normal priority for bug instances.
    */
  val Normal = Priority("medium")

  /**
    * High priority for bug instances.
    */
  val High = Priority("high")
}
