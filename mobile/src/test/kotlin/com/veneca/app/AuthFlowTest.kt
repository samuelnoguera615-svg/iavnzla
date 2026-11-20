package com.veneca.app

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

fun main() {
    val clock = Clock.fixed(Instant.parse("2026-11-13T12:00:00Z"), ZoneOffset.UTC)
    val repository = FakeAuthRepository()
    val google = QueueGoogleAuthGateway()
    val flow = AuthFlow(google, repository, clock)

    google.next = identity("minor-1", "minor@example.com")
    check(flow.signIn() is AuthResult.NeedsRegistration)

    google.next = identity("minor-1", "minor@example.com")
    val minorResult = flow.register(
        fullName = "Usuario Menor",
        identityNumber = "V-12345678",
        birthDate = LocalDate.of(2012, 1, 1),
        guardianEmail = "adult@example.com"
    )
    check(minorResult is AuthResult.WaitingForFamily)
    check(repository.profiles["minor-1"]?.status == AccountStatus.PENDING_GUARDIAN)
    check(repository.rawIdentityNumber == "V12345678")

    google.next = identity("minor-1", "minor@example.com")
    check(flow.signIn() is AuthResult.WaitingForFamily)

    google.next = identity("adult-1", "adult@example.com")
    flow.register(
        fullName = "Usuario Adulto",
        identityNumber = "E1234567",
        birthDate = LocalDate.of(1990, 1, 1)
    )

    google.next = identity("adult-1", "adult@example.com")
    flow.approveFamilyMember("minor-1")
    check(repository.profiles["minor-1"]?.status == AccountStatus.ACTIVE)

    google.next = identity("minor-1", "minor@example.com")
    check(flow.signIn() is AuthResult.SignedIn)

    google.next = identity("seventeen-1", "seventeen@example.com")
    val seventeenResult = flow.register(
        fullName = "Usuario de 17",
        identityNumber = "V12345679",
        birthDate = LocalDate.of(2009, 11, 13)
    )
    check(seventeenResult is AuthResult.SignedIn)

    println("AuthFlow checks passed")
}

private fun identity(userId: String, email: String) =
    GoogleIdentity(userId, email, emailVerified = true, displayName = userId)

private class QueueGoogleAuthGateway : GoogleAuthGateway {
    lateinit var next: GoogleIdentity
    override fun signIn(): GoogleIdentity = next
}

private class FakeAuthRepository : AuthRepository {
    val profiles = mutableMapOf<String, AuthProfile>()
    var rawIdentityNumber: String? = null

    override fun findProfile(userId: String): AuthProfile? = profiles[userId]

    override fun register(
        identity: GoogleIdentity,
        fullName: String,
        identityNumber: String,
        birthDate: LocalDate,
        guardianEmail: String?
    ): AuthProfile {
        rawIdentityNumber = identityNumber
        val profile = AuthProfile(
            userId = identity.userId,
            email = identity.email,
            fullName = fullName,
            birthDate = birthDate,
            status = if (guardianEmail == null) AccountStatus.ACTIVE else AccountStatus.PENDING_GUARDIAN,
            guardianEmail = guardianEmail
        )
        profiles[identity.userId] = profile
        return profile
    }

    override fun approveMinorAccess(guardianUserId: String, minorUserId: String) {
        val minor = profiles[minorUserId] ?: error("Unknown minor")
        check(minor.guardianEmail == profiles[guardianUserId]?.email)
        profiles[minorUserId] = minor.copy(status = AccountStatus.ACTIVE)
            .copy(guardianApproved = true)
    }
}
