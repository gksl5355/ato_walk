alter table meetups
    add column dog_size varchar(32),
    add column sociability_level varchar(32),
    add column reactivity_level varchar(32),
    add column neutered boolean,
    add constraint meetups_dog_size_check check (dog_size in ('SMALL', 'MEDIUM', 'LARGE')),
    add constraint meetups_sociability_level_check check (sociability_level in ('LOW', 'MEDIUM', 'HIGH')),
    add constraint meetups_reactivity_level_check check (reactivity_level in ('LOW', 'MEDIUM', 'HIGH'));
