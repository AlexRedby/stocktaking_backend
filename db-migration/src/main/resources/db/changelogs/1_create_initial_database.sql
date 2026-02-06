CREATE SCHEMA stocktaking;

CREATE TABLE stocktaking.item (
    id         integer primary key,
    name       text,
    short_name text,
    base_price integer,
    wiki_link  text,
    updated    timestamp
);
