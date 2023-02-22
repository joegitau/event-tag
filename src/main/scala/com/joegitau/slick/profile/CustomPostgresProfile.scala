package com.joegitau.slick.profile

import com.github.tminglei.slickpg._
import play.api.libs.json.{JsValue, Json}

trait CustomPostgresProfile extends ExPostgresProfile
  with PgArraySupport
  with PgDate2Support
  with PgHStoreSupport
  with PgJsonSupport {

  def pgjson: String = "jsonb"

  object CustomAPI extends API
    with ArrayImplicits
    with DateTimeImplicits
    with JsonImplicits
    with HStoreImplicits {

    implicit val strListTypeMapper: DriverJdbcType[List[String]] =
      new SimpleArrayJdbcType[String]("text").to(_.toList)

    implicit val longListTypeMapper: DriverJdbcType[List[Long]] =
      new SimpleArrayJdbcType[Long]("bigint").to(_.toList)

    implicit val playJsonArrayTypeMapper: DriverJdbcType[List[JsValue]] =
      new AdvancedArrayJdbcType[JsValue](
        pgjson,
        s => utils.SimpleArrayUtils.fromString[JsValue](Json.parse)(s).orNull,
        v => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)

  }
}

object CustomPostgresProfile extends CustomPostgresProfile
