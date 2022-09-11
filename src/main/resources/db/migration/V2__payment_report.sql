create table payment_report
(
    id                     uuid not null primary key,
    payment_id             uuid not null,
    new_status             varchar(255),
    timestamp              timestamp,
    created_at             timestamp not null
);