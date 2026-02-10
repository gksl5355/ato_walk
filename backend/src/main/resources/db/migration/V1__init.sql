create table users (
    id bigserial primary key,
    email varchar(255) not null unique,
    status varchar(32) not null,
    created_at timestamptz not null,
    constraint users_status_check check (status in ('ACTIVE', 'BLOCKED'))
);

create table dogs (
    id bigserial primary key,
    user_id bigint not null references users (id),
    name varchar(255) not null,
    breed varchar(255) not null,
    size varchar(32) not null,
    neutered boolean not null,
    sociability_level varchar(32) not null,
    reactivity_level varchar(32) not null,
    notes varchar(255),
    created_at timestamptz not null,
    constraint dogs_size_check check (size in ('SMALL', 'MEDIUM', 'LARGE')),
    constraint dogs_sociability_level_check check (sociability_level in ('LOW', 'MEDIUM', 'HIGH')),
    constraint dogs_reactivity_level_check check (reactivity_level in ('LOW', 'MEDIUM', 'HIGH'))
);

create index dogs_user_id_idx on dogs (user_id);

create table meetups (
    id bigserial primary key,
    host_user_id bigint not null references users (id),
    title varchar(255) not null,
    description varchar(255),
    location varchar(255) not null,
    max_participants integer not null,
    scheduled_at timestamptz not null,
    status varchar(32) not null,
    created_at timestamptz not null,
    constraint meetups_status_check check (status in ('RECRUITING', 'ENDED', 'CANCELED'))
);

create index meetups_host_user_id_idx on meetups (host_user_id);

create table participations (
    id bigserial primary key,
    meetup_id bigint not null references meetups (id),
    user_id bigint not null references users (id),
    status varchar(32) not null,
    created_at timestamptz not null,
    constraint participations_status_check check (status in ('REQUESTED', 'APPROVED', 'REJECTED'))
);

create index participations_meetup_id_idx on participations (meetup_id);
create index participations_user_id_idx on participations (user_id);

create table communications (
    id bigserial primary key,
    meetup_id bigint not null references meetups (id),
    content varchar(255) not null,
    created_at timestamptz not null
);

create index communications_meetup_id_idx on communications (meetup_id);

create table reports (
    id bigserial primary key,
    reporter_user_id bigint not null references users (id),
    reported_user_id bigint not null references users (id),
    meetup_id bigint references meetups (id),
    reason varchar(255) not null,
    created_at timestamptz not null
);

create index reports_reporter_user_id_idx on reports (reporter_user_id);
create index reports_reported_user_id_idx on reports (reported_user_id);
create index reports_meetup_id_idx on reports (meetup_id);

create table blocks (
    id bigserial primary key,
    blocker_user_id bigint not null references users (id),
    blocked_user_id bigint not null references users (id),
    created_at timestamptz not null
);

create index blocks_blocker_user_id_idx on blocks (blocker_user_id);
create index blocks_blocked_user_id_idx on blocks (blocked_user_id);

create table reviews (
    id bigserial primary key,
    participation_id bigint not null references participations (id),
    reviewer_user_id bigint not null references users (id),
    reviewee_user_id bigint not null references users (id),
    rating integer not null,
    content varchar(255),
    created_at timestamptz not null,
    constraint reviews_participation_reviewer_unique unique (participation_id, reviewer_user_id)
);

create index reviews_participation_id_idx on reviews (participation_id);
create index reviews_reviewer_user_id_idx on reviews (reviewer_user_id);
create index reviews_reviewee_user_id_idx on reviews (reviewee_user_id);
