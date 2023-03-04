-- database setup
DROP DATABASE IF EXISTS event_tag_db;
CREATE DATABASE event_tag_db;
\c event_tag_db;

-- events
CREATE TABLE "events" (
    "id"          BIGSERIAL   PRIMARY KEY,
    "title"       TEXT        NOT NULL,
    "description" TEXT        NOT NULL,
    "location"    VARCHAR     NOT NULL,
    "start_date"  timestamptz NOT NULL,
    "end_date"    timestamptz NOT NULL,
    "organizer"   VARCHAR,
    "created"     timestamptz DEFAULT CURRENT_TIMESTAMP,
    "modified"    timestamptz
);

-- attendees
CREATE TABLE attendees (
    "id"        BIGSERIAL   PRIMARY KEY,
    "first_name" VARCHAR     NOT NULL ,
    "last_name"  VARCHAR     NOT NULL,
    "company"    VARCHAR,
    "email"      VARCHAR     NOT NULL UNIQUE ,
    "created"    timestamptz DEFAULT CURRENT_TIMESTAMP,
    "modified"   timestamptz
);

-- attendee_event_relation (junction table establishing a many-to-many relationship)
CREATE TABLE attendee_event_relations (
    "event_id"    BIGSERIAL     NOT NULL,
    "attendee_id" BIGSERIAL     NOT NULL,
    PRIMARY KEY   (attendee_id, event_id), -- composite key
    FOREIGN KEY   (event_id)    REFERENCES events (id)    ON DELETE CASCADE,
    FOREIGN KEY   (attendee_id) REFERENCES attendees (id) ON DELETE CASCADE
);

-- attendance
CREATE TABLE attendances (
    "event_id"      BIGSERIAL     NOT NULL ,
    "attendee_id"   BIGSERIAL     NOT NULL,
    "checkin_time"  timestamptz,
    "checkout_time" timestamptz,
    PRIMARY KEY     (event_id, attendee_id), -- composite key
    FOREIGN KEY     (event_id)    REFERENCES events (id),
    FOREIGN KEY     (attendee_id) REFERENCES attendees (id)
);
