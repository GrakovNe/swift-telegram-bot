create table payment
(
    id                     uuid not null primary key,
    last_modified_at       timestamp,
    payment_last_update_at timestamp,
    status                 varchar(255)
);

create table user_reference
(
    id     varchar(255) not null primary key,
    source varchar(255)
);
create table user_reference_subscribed_payments
(
    user_reference_id   varchar(255) not null constraint fktchwt4shb18etng2foolaqolb references user_reference,
    subscribed_payments uuid
);
