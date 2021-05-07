create table hibernate_sequence
(
    next_val bigint
) engine = InnoDB;
insert into hibernate_sequence (next_val)
VALUES (1);
create table building
(
    dtype       varchar(31) not null,
    id          bigint      not null auto_increment,
    finished_at datetime(6),
    level       integer,
    started_at  datetime(6),
    kingdom_id  bigint,
    primary key (id)
) engine = InnoDB;
create table kingdom
(
    id         bigint       not null auto_increment,
    name       varchar(255) not null,
    location_x integer,
    location_y integer,
    primary key (id)
) engine = InnoDB;
create table location
(
    x integer not null,
    y integer not null,
    primary key (x, y)
) engine = InnoDB;
create table resource
(
    dtype        varchar(31) not null,
    id           bigint      not null auto_increment,
    amount       integer,
    generation   integer,
    last_updated datetime(6),
    kingdom_id   bigint,
    primary key (id)
) engine = InnoDB;
create table troop
(
    id          bigint not null auto_increment,
    attack      integer,
    defense     integer,
    finished_at datetime(6),
    hp          integer,
    level       integer,
    started_at  datetime(6),
    kingdom_id  bigint,
    primary key (id)
) engine = InnoDB;
create table user
(
    id         bigint       not null,
    username   varchar(255) not null,
    password   varchar(255),
    kingdom_id bigint,
    primary key (id)
) engine = InnoDB;
