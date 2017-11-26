-- sql initialization script

-- create a database
CREATE DATABASE seckill;

-- use the database created just now
USE seckill;

-- create second kill store table

CREATE TABLE seckill(

'seckill_id' BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
'name' VARCHAR(120) NOT NULL COMMENT '商品名称',
  'number' INT NOT NULL COMMENT '库存数量',
  'start_time' TIMESTAMP NOT NULL COMMENT '秒杀开启时间',
  'end_time' TIMESTAMP NOT NULL COMMENT '秒杀结束时间',
  'create_time' TIMESTAMP NOT NULL DEFAULT current_timestamp COMMENT '创建时间',
  PRIMARY KEY (seckill_id),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time),
  KEY idx_create_time(create_time)

)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=UTF8 COMMENT='秒杀库存表';

-- initial some data
INSERT INTO
  seckill(name, number, start_time, end_time)
VALUES
  ('1000元秒杀iphone x', 100, '2017-10-15 00:00:00', '2017-10-16 00:00:00'),
  ('500元秒杀iphone 8 plus', 200, '2017-10-15 00:00:00', '2017-10-16 00:00:00'),
  ('300元秒杀mi mix2', 300, '2017-10-15 00:00:00', '2017-10-16 00:00:00'),
  ('200元秒杀meizu', 400, '2017-10-15 00:00:00', '2017-10-16 00:00:00');

-- design second kill successfully details table
-- need users logging information
CREATE TABLE success_killed(

  'seckill_id' BIGINT NOT NULL COMMENT '秒杀商品id',
  'user_phone' BIGINT NOT NULL COMMENT '用户手机号',
  'state' TINYINT NOT NULL COMMENT '状态标识：-1：失败 0：秒杀成功 1：已付款 2：已发货',
  `create_time` TIMESTAMP NOT NULL COMMENT '创建时间',
  PRIMARY KEY (seckill_id, user_phone),/*union primary key*/
  KEY idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET = utf8 COMMENT '秒杀成功明细表';