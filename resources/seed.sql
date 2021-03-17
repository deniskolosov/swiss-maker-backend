-- tournament
truncate table tournament cascade;
insert into tournament ("id", "name", "num_of_rounds")
values (1, 'First tournament', 1);

-- player
truncate table player cascade;
insert into player (id, "name", rating, tournament_id)
values ('c6d60a7a-8997-419f-8a34-5edf719f0b5b', 'Ivan Ivanov', 1000, 1),
       ('1ef53c2c-a326-4d43-8813-67f0c75ac055', 'Petr Petrov', 1200, 1),
       ('c01c099c-1ec1-4350-a274-6f16772b1b54', 'Nikolay Kozlov', 1300, 1),
       ('a0758ad6-1e7e-459c-9a80-5b8344c74978', 'Fedor Sokolov', 1450, 1),
       ('df2330a4-0f4d-4603-a3a6-502b6a990dd1', 'Semen Fedorov', 1500, 1);

