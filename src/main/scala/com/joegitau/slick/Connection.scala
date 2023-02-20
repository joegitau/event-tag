package com.joegitau.slick

import com.joegitau.slick.profile.CustomPostgresProfile
import com.joegitau.slick.profile.CustomPostgresProfile.api._

object Connection {
  val Db: CustomPostgresProfile.backend.Database = Database.forConfig("postgres")
}
