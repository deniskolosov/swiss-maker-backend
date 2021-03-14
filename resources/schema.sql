-- Drop tables
drop sequence if exists tournament_id_seq cascade;
drop sequence if exists pairing_id_seq cascade;
drop table  if exists tournament cascade;
drop table  if exists player cascade;
drop table  if exists pairing cascade;

-- Create schema

create table tournament (
  id serial not null primary key,
  "name" text,
  num_of_rounds int not null,
  current_round int check (current_round >= 0) default 0,
  unique(id)
);

create table player (
  id text not null primary key,
  "name" text,
  rating int check (rating >= 0) default 0,
  current_score int check (current_score >= 0) default 0,
  tournament_id int references tournament(id) on delete cascade,
  unique(id)
);

create table pairing (
  id serial not null primary key,
  white_id text not null references player(id) on delete cascade,
  black_id text not null references player(id) on delete cascade,
  board_no int not null check (board_no > 0),
  result real,
  tournament_id int references tournament(id) on delete cascade,
  round_no int not null check (round_no > 0),
  unique(id),
  check (white_id <> black_id)
);
