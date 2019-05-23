begin;

create sequence users_id;
create table    users       (id         integer default nextval('users_id') primary key,
							 version    integer default 1 not null,
                             email      varchar(250),
                             name       varchar(64),
                             passwd     varchar(64)
                             );
                             
create sequence recipes_id;
create table    recipes     (id         integer default nextval('recipes_id') primary key,
                             user_id    integer references users(id) on delete cascade not null,
                             version    integer default 1 not null,
                             ingredients text,
                             instructions text,
                             added_on   timestamp(0),
                             tags       text
                            );

create sequence comments_id;
create table    comments    (id         integer default nextval('comments_id') primary key,
                             user_id    integer references users(id) on delete cascade not null,
                             recipe_id  integer references recipes(id) on delete cascade not null,
                             version    integer default 1 not null,
                             body       text,
                             rating     integer,
                             added_on   timestamp(0)
                             );

create function version_update() returns trigger as $vup$
begin
	NEW.version := NEW.version + 1;
	return NEW;
end; 
$vup$ language 'plpgsql';

create trigger version_updater before update on users
for each row execute procedure version_update();
                             
create trigger version_updater before update on recipes
for each row execute procedure version_update();

create trigger version_updater before update on comments
for each row execute procedure version_update();

commit;