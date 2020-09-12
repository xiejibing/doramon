create database miaosha;

create table goods
(
    id           bigint auto_increment comment '商品id'
        primary key,
    goods_name   varchar(16)                 null comment '商品名称',
    goods_title  varchar(64)                 null comment '商品标题',
    goods_img    varchar(64)                 null comment '商品图片',
    goods_detail longtext                    null comment '详情介绍',
    goods_price  decimal(10, 2) default 0.00 null comment '商品单价',
    goods_stock  int            default 0    null comment '商品库存，－１表示没有限制'
)
    comment '商品表';

create table miaosha_goods
(
    id            bigint auto_increment
        primary key,
    goods_id      bigint                      null comment '商品id',
    miaosha_price decimal(10, 2) default 0.00 null comment '秒杀价',
    stock_count   int                         null comment '库存数量',
    start_date    datetime                    null comment '秒杀开始时间',
    end_date      datetime                    null comment '秒杀结束时间'
)
    comment '秒杀商品表';

create table miaosha_order
(
    id       bigint auto_increment
        primary key,
    user_id  bigint null comment '用户ｉｄ',
    order_id bigint null comment '订单id',
    goods_id bigint null comment '商品id',
    constraint u_uid_gid
        unique (user_id, goods_id)
)
    comment '秒杀订单';

create table miaosha_user
(
    id              bigint        not null comment '用户id，手机号码'
        primary key,
    nickname        varchar(255)  not null,
    password        varchar(32)   null,
    salt            varchar(10)   null,
    head            varchar(128)  null comment '头像',
    register_date   datetime      null comment '注册时间',
    last_login_date datetime      null comment '上次登录时间',
    login_count     int default 0 null comment '登陆次数'
);

create table order_info
(
    id               bigint auto_increment
        primary key,
    user_id          bigint                      null comment '用户id',
    goods_id         bigint                      null comment '商品id',
    delivery_addr_id bigint                      null comment '收货地址id',
    goods_name       varchar(16)                 null comment '商品名称
',
    goods_count      int            default 0    null comment '商品数量',
    goods_price      decimal(10, 2) default 0.00 null comment '商品单价',
    order_channel    tinyint        default 0    null comment '1pc,2andriod,3ios',
    status           tinyint        default 0    null comment '订单状态，0新建未支付，１已支付，２已发货，３已收货，４已退款，５已完成',
    create_date      datetime                    null comment '订单的创建时间',
    pay_date         datetime                    null comment '支付时间'
)
    comment '订单信息';

