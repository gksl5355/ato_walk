create table chat_messages (
    id bigserial primary key,
    meetup_id bigint not null references meetups (id),
    sender_user_id bigint not null references users (id),
    sender_nickname varchar(255) not null,
    content varchar(255) not null,
    created_at timestamptz not null
);

create index chat_messages_meetup_id_idx on chat_messages (meetup_id);
