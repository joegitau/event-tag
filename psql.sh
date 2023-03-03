#!/usr/bin/env bash

# *** ** "container_name" psql -U "POSTGRES_USER" "db_name"
docker exec -it event_tag_c psql -U postgres -d event_tag_db
