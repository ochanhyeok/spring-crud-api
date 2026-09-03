alter table member
    add column created_at  datetime(6) not null default current_timestamp(6),
    add column modified_at datetime(6) not null default current_timestamp(6);

alter table post
    add column created_at  datetime(6) not null default current_timestamp(6),
    add column modified_at datetime(6) not null default current_timestamp(6);

alter table comment
    add column created_at  datetime(6) not null default current_timestamp(6),
    add column modified_at datetime(6) not null default current_timestamp(6);

alter table post_like
    add column created_at  datetime(6) not null default current_timestamp(6),
    add column modified_at datetime(6) not null default current_timestamp(6);

alter table comment_like
    add column created_at  datetime(6) not null default current_timestamp(6),
    add column modified_at datetime(6) not null default current_timestamp(6);
