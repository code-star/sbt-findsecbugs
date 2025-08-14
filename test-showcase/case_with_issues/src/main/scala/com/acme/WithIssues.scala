package com.acme

object WithIssues {
  import scala.util.Random

  def generateSecretToken(): String = {
    val result = Seq.fill(16)(Random.nextInt)
    result.map("%02x" format _).mkString
  }
}
