alter table users
    add column password_hash varchar(255);

update users
set password_hash = '$2b$12$xmBKFpuJFhwyMSZFNKotbeilxIvvttlUl.m09Gi0F1T9EsEfQnQcm'
where password_hash is null;

alter table users
    alter column password_hash set not null;

insert into users (email, password_hash, status, created_at)
select 'ato@ato.com',
       '$2b$12$xmBKFpuJFhwyMSZFNKotbeilxIvvttlUl.m09Gi0F1T9EsEfQnQcm',
       'ACTIVE',
       now()
where not exists (
    select 1
    from users
    where email = 'ato@ato.com'
);

insert into dogs (
    user_id,
    name,
    breed,
    size,
    neutered,
    sociability_level,
    reactivity_level,
    notes,
    created_at
)
select u.id,
       'Ato',
       'Pomeranian',
       'SMALL',
       true,
       'MEDIUM',
       'LOW',
       'Dummy seed dog profile',
       now()
from users u
where u.email = 'ato@ato.com'
  and not exists (
      select 1
      from dogs d
      where d.user_id = u.id
  );
