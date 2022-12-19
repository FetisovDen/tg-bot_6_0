-- liquibase formatted sql

-- changeset dfetisov:1
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='notification_task'
CREATE TABLE notification_task
(
    id           BIGINT PRIMARY KEY generated always as identity,
    chat_id      BIGINT    NOT NULL,
    date_time    TIMESTAMP NOT NULL,
    text_massage TEXT      NOT NULL
)
