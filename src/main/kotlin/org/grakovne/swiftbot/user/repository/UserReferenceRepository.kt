package org.grakovne.swiftbot.user.repository

import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserReferenceRepository : JpaRepository<UserReference, UUID>