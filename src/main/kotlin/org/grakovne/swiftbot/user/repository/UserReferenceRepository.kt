package org.grakovne.swiftbot.user.repository

import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.data.jpa.repository.JpaRepository

interface UserReferenceRepository : JpaRepository<UserReference, String>