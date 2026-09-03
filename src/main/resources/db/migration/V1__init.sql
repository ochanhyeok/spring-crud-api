create table member (
    id          bigint         not null auto_increment,
    login_id    varchar(50)    not null,
    name        varchar(50)    not null,
    password    varchar(100)   not null,
    primary key (id),
    constraint uk_member_login_id unique (login_id)
) engine = innodb;

create table post (
    id           bigint         not null auto_increment,
    title        varchar(100)   not null,
    content      varchar(3000)  not null,
    view_count   bigint         not null default 0,
    member_id    bigint         not null,
    deleted_at   datetime(6)    null,
    primary key (id),
    constraint fk_post_member foreign key (member_id) references member (id)
) engine = innodb;

create table comment (
    id          bigint        not null auto_increment,
    content     varchar(500)  not null,
    post_id     bigint        not null,
    member_id   bigint        not null,
    deleted_at  datetime(6)   null,
    primary key (id),
    constraint fk_comment_post   foreign key (post_id)   references post (id),
    constraint fk_comment_member foreign key (member_id) references member (id)
) engine = innodb;

create table post_like (
    id          bigint  not null auto_increment,
    post_id     bigint  not null,
    member_id   bigint  not null,
    primary key (id),
    constraint uk_post_like_post_member unique (post_id, member_id),
    constraint fk_post_like_post   foreign key (post_id)   references post (id),
    constraint fk_post_like_member foreign key (member_id) references member (id)
) engine = innodb;

create table comment_like (
    id          bigint not null auto_increment,
    comment_id  bigint not null,
    member_id   bigint not null,
    primary key (id),
    constraint uk_comment_like_comment_member unique (comment_id, member_id),
    constraint fk_comment_like_comment foreign key (comment_id) references comment (id),
    constraint fk_comment_like_member  foreign key (member_id)  references member (id)
) engine = innodb;