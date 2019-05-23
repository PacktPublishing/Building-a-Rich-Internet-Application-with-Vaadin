begin;

create sequence users_id;
create table    users       (id         integer default nextval('users_id') primary key,
							 version    integer default 1,
                             email      varchar(250),
                             name       varchar(64),
                             passwd     varchar(64)
                             );
                             
create sequence recipes_id;
create table    recipes     (id         integer default nextval('recipes_id') primary key,
                             user_id    integer references users(id) on delete cascade,
                             version    integer default 1,
                             title		varchar(128),
                             ingredients text,
                             instructions text,
                             added_on   timestamp(0),
                             tags       text
                            );

create sequence comments_id;
create table    comments    (id         integer default nextval('comments_id') primary key,
                             user_id    integer references users(id) on delete cascade,
                             recipe_id  integer references recipes(id) on delete cascade,
                             version    integer default 1,
                             body       text,
                             rating     integer,
                             added_on   timestamp(0)
                             );

create function version_update() returns trigger as $vup$
begin
	if NEW.version IS NULL then
	  NEW.version := 0;
	end if;
	NEW.version := NEW.version + 1;
	return NEW;
end; 
$vup$ language 'plpgsql';

create trigger version_updater before update or insert on users
for each row execute procedure version_update();
                             
create trigger version_updater before update or insert on recipes
for each row execute procedure version_update();

create trigger version_updater before update or insert on comments
for each row execute procedure version_update();

commit;