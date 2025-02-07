DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户编号',
  `account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户密码',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '头像',
  `nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '手机号',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人编号',
  `updator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人编号',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP (3),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_account`(`account`) USING BTREE,
  INDEX `idx_user_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COMMENT = '用户表' ROW_FORMAT = DYNAMIC;


CREATE TABLE red_envelope_info (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  total_amount decimal(6,2) NOT NULL COMMENT '红包金额(元)',
  use_amount decimal(6,2) NOT NULL COMMENT '已抢红包金额(元)',
  remaining_amount decimal(6,2) NOT NULL COMMENT '剩余红包金额(元)',
  total_num int(11) NOT NULL COMMENT '红包总数量(个)',
  use_num int(11) NOT NULL COMMENT '已抢数量(个)',
  remaining_num int(11) NOT NULL COMMENT '剩余数量(个)',
  status tinyint NOT NULL DEFAULT '0' COMMENT '状态 默认0(没人抢)，1-进行中 2-已抢完',
  creator varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  create_time timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='红包信息表';

CREATE TABLE red_envelope_record (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  red_envelope_id bigint DEFAULT NULL COMMENT '红包的ID',
  before_amount decimal(10,2) DEFAULT NULL COMMENT '扣减前的金额',
  amount decimal(10,2) DEFAULT NULL COMMENT '扣减的金额',
  after_amount decimal(10,2) DEFAULT NULL COMMENT '扣减后的金额',
  before_num bigint DEFAULT NULL COMMENT '领取前的次数（红包可领取次数）',
  after_num bigint DEFAULT NULL COMMENT '领取后的次数（红包可领取次数）',
  create_time timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='红包领取记录表';

CREATE TABLE use_red_envelope_record (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  red_envelope_id bigint(20) NOT NULL COMMENT '红包id',
  amount decimal(10,2) NOT NULL COMMENT '用户领取红包金额',
  befer_amount decimal(10,2) DEFAULT NULL COMMENT '用户领取红包之前的金额',
  after_amount decimal(10,2) DEFAULT NULL COMMENT '用户领取红包后的金额',
  creator varchar(64) NOT NULL COMMENT '创建人',
  create_time timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  receive_record_id bigint(20) DEFAULT NULL COMMENT '领取红包的流水ID(red_envelope_record的ID)',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户领取红包记录表';


INSERT INTO `user` (user_no, account, password, image, nick_name, phone, amount, creator, updator, create_time, update_time) VALUES ('10086', 'Asen1', '202cb962ac59075b964b07152d234b70', NULL, 'Asen1', NULL, 0, 'Asen1', NULL, NOW(),NOW());
INSERT INTO `user` (user_no, account, password, image, nick_name, phone, amount, creator, updator, create_time, update_time) VALUES ('10086', 'Asen2', '202cb962ac59075b964b07152d234b70', NULL, 'Asen2', NULL, 0, 'Asen2', NULL, NOW(),NOW());
INSERT INTO `user` (user_no, account, password, image, nick_name, phone, amount, creator, updator, create_time, update_time) VALUES ('10086', 'Asen3', '202cb962ac59075b964b07152d234b70', NULL, 'Asen3', NULL, 0, 'Asen3', NULL, NOW(),NOW());
INSERT INTO `user` (user_no, account, password, image, nick_name, phone, amount, creator, updator, create_time, update_time) VALUES ('10086', 'Asen4', '202cb962ac59075b964b07152d234b70', NULL, 'Asen4', NULL, 0, 'Asen4', NULL, NOW(),NOW());

INSERT INTO red_envelope_info (total_amount, use_amount, remaining_amount, total_num, use_num, remaining_num, `status`, creator, create_time) VALUES (500, 0, 500, 100, 0, 100, 0, '', NOW());
INSERT INTO red_envelope_info (total_amount, use_amount, remaining_amount, total_num, use_num, remaining_num, `status`, creator, create_time) VALUES (500, 0, 500, 100, 0, 100, 0, '', NOW());
INSERT INTO red_envelope_info (total_amount, use_amount, remaining_amount, total_num, use_num, remaining_num, `status`, creator, create_time) VALUES (500, 0, 500, 100, 0, 100, 0, '', NOW());
INSERT INTO red_envelope_info (total_amount, use_amount, remaining_amount, total_num, use_num, remaining_num, `status`, creator, create_time) VALUES (500, 0, 500, 100, 0, 100, 0, '', NOW());
INSERT INTO red_envelope_info (total_amount, use_amount, remaining_amount, total_num, use_num, remaining_num, `status`, creator, create_time) VALUES (500, 0, 500, 100, 0, 100, 0, '', NOW());
