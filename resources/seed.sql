-- tournament
truncate table tournament cascade;
insert into tournament ("id", "name", "num_of_rounds")
values (1, 'First tournament', 1);

-- player
truncate table player cascade;
insert into player (id, "name", rating, tournament_id)
values ('867ed4bf-4628-48f4-944d-e6b7786bfa92', 'Ivan Ivanov', 1000, 1),
       ('867ed4b1-4628-48f4-944d-e6b7786bfa92', 'Petr Petrov', 1200, 1),
       ('867ec4b2-4628-48f4-944d-e6b7786bfa92', 'Nikolay Kozlov', 1300, 1),
       ('867e34b3-4628-48f4-944d-e6b7786bfa92', 'Fedor Sokolov', 1450, 1),
       ('867ee4b4-4628-48f4-944d-e6b7786bfa92', 'Semen Fedorov', 1500, 1);
