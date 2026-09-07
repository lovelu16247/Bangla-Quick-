package com.example.security

import java.security.MessageDigest
import java.util.UUID

object SecurityManager {

  fun generateSalt(): String = UUID.randomUUID().toString().take(12)

  fun hashPassword(password: String, salt: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest((password + salt).toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { "%02x".format(it) }
  }

  fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
    return hashPassword(password, salt) == expectedHash
  }

  /**
   * Anti-cheat validation for quiz score submission.
   * Ensures that score submitted matches computed correctness and time taken is realistic.
   */
  fun validateQuizScore(
    totalQuestions: Int,
    correctAnswers: Int,
    pointsPerQuestion: Int,
    submittedScore: Int,
    elapsedSeconds: Long
  ): Boolean {
    val maxPossibleScore = totalQuestions * pointsPerQuestion
    if (submittedScore > maxPossibleScore || submittedScore < 0) return false
    val expectedScore = correctAnswers * pointsPerQuestion
    if (submittedScore != expectedScore) return false
    // Minimum 0.5s per question to prevent automated bot scraping
    val minRequiredSeconds = (totalQuestions * 0.5).toLong()
    if (elapsedSeconds < minRequiredSeconds && totalQuestions > 3) return false
    return true
  }

  /**
   * Sanitizes input to prevent HTML/script injection
   */
  fun sanitizeText(input: String): String {
    return input.trim()
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
      .replace("'", "&#x27;")
  }
}
