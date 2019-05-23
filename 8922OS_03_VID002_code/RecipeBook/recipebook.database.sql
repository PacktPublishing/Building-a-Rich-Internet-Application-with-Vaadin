create sequence users_id;
create table    users       (id         integer default nextval('users_id') primary key,
                             email      varchar(250),
                             name       varchar(64),
                             passwd     varchar(64)
                             );
                             
create sequence recipes_id;
create table    recipes     (id         integer default nextval('recipes_id') primary key,
                             user_id    integer references users(id) not null on delete cascade,
                             ingredients text,
                             instructions text,
                             added_on   timestamp(0),
                             tags       text
                            );

create sequence comments_id;
create table    comments    (id         integer default nextval('comments_id') primary key,
                             user_id    integer references users(id) not null on delete cascade,
                             recipe_id  integer references recipes(id) not null on delete cascade,
                             body       text,
                             rating     integer
                             added_on   timestamp(0)
                             );