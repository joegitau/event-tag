lazy val AkkaVersion = "2.7.0"
lazy val SlickVersion = "3.4.1"
lazy val CirceVersion = "0.14.4"
lazy val SlickPgVersion = "0.21.1"
lazy val AkkaHttpVersion = "10.4.0"
lazy val PostgresqlVersion = "42.5.4"

lazy val root = (project in file("."))
  .settings(
    name         := "event-tag",
    version      := "1.0.0",
    scalaVersion := "2.13.10",
    libraryDependencies ++=Seq(
      // actors
      "com.typesafe.akka"   %% "akka-actor-typed"           % AkkaVersion,
      // db
      "com.typesafe.slick"  %% "slick"                      % SlickVersion,
      "org.postgresql"      % "postgresql"                  % PostgresqlVersion,
      "com.typesafe.slick"  %% "slick-hikaricp"             % "3.4.1",
      "com.github.tminglei" %% "slick-pg"                   % SlickPgVersion,
      "com.github.tminglei" %% "slick-pg_spray-json"        % SlickPgVersion,
      "com.github.tminglei" %% "slick-pg_circe-json"        % SlickPgVersion,
      "com.github.tminglei" %% "slick-pg_play-json"         % SlickPgVersion,
      // HTTP
      "com.typesafe.akka"   %% "akka-http"                  % AkkaHttpVersion,
      // streams
      "com.typesafe.akka"   %% "akka-stream"                % AkkaVersion,
      // serialization and marshalling/ un-marshalling
      "com.typesafe.akka"   %% "akka-serialization-jackson" % AkkaVersion,
      "com.github.tminglei" %% "slick-pg_play-json"         % SlickPgVersion,
      "com.typesafe.akka"   %% "akka-http-spray-json"       % AkkaHttpVersion,
      "io.circe"            %% "circe-core"                 % CirceVersion,
      "io.circe"            %% "circe-generic"              % CirceVersion,
      "io.circe"            %% "circe-parser"               % CirceVersion,
      "de.heikoseeberger"   %% "akka-http-circe"            % "1.39.2",
      // logging
      "com.typesafe.akka"   %% "akka-slf4j"                 % AkkaVersion,
      "ch.qos.logback"      % "logback-classic"             % "1.2.11",
      // tests
      "com.typesafe.akka"   %% "akka-actor-testkit-typed"   % AkkaVersion % Test,
      "com.typesafe.akka"   %% "akka-stream-testkit"        % AkkaVersion % Test,
      "org.scalatest"       %% "scalatest"                  % "3.2.15"    % Test,
    )
  )
