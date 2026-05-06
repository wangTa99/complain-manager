CREATE TABLE `complaint_order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `complaint_no` varchar(255) NOT NULL DEFAULT '' COMMENT '客诉单号',
  `idempotent_key` varchar(32) NOT NULL DEFAULT '' COMMENT '业务幂等key',
  `super_ticket_no` varchar(255) NOT NULL DEFAULT '' COMMENT '超级工单号',
  `so_no` varchar(255) NOT NULL DEFAULT '' COMMENT '服务单号',
  `vid` varchar(20) NOT NULL DEFAULT '' COMMENT '车辆vid',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '客诉单状态',
  `responsibility` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有责，0 无责 1 有责',
  `org_id` varchar(255) NOT NULL DEFAULT '' COMMENT '门店Id',
  `complaint_type` int(11) NOT NULL DEFAULT '0' COMMENT '投诉分类 1 产品投诉 2 服务投诉',
  `risk_level` int(11) NOT NULL DEFAULT '0' COMMENT '风险等级 1 2 3 4',
  `car_type` varchar(32) NOT NULL DEFAULT '' COMMENT '车型',
  `car_no` varchar(20) NOT NULL DEFAULT '' COMMENT '车牌号',
  `contact_name_c` varchar(255) NOT NULL DEFAULT '' COMMENT '联系人姓名密文',
  `contact_gender` tinyint(4) NOT NULL DEFAULT '0' COMMENT '联系人性别 0 默认 1 男 2 女',
  `contact_phone_c` varchar(255) NOT NULL DEFAULT '' COMMENT '联系人电话密文',
  `contact_phone_sufix` int(10) NOT NULL DEFAULT '0' COMMENT '手机号后4位',
  `contact_phone_md5` varchar(32) NOT NULL DEFAULT '' COMMENT '联系人电话md5',
  `vin_sufix` varchar(32) NOT NULL DEFAULT '' COMMENT 'vin后6位',
  `reminder_times` int(11) NOT NULL DEFAULT '0' COMMENT '催单次数',
  `problem_desc` varchar(1024) NOT NULL DEFAULT '' COMMENT '问题描述',
  `user_demand` varchar(1024) NOT NULL DEFAULT '' COMMENT '用户诉求',
  `city_id` varchar(255) NOT NULL DEFAULT '' COMMENT '城市id',
  `zone_id` varchar(255) NOT NULL DEFAULT '' COMMENT '大区id',
  `little_zone_id` varchar(255) NOT NULL DEFAULT '' COMMENT '小区id',
  `problem_category` varchar(1024) NOT NULL DEFAULT '' COMMENT '问题类目',
  `only_view` tinyint(1) NOT NULL DEFAULT '0' COMMENT '投诉单是否门店仅查阅, 0-否，需要门店处理, 1-仅查阅,不需要门店处理',
  `test_tag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '测试标识, 0-非测试环境, 1-是测试环境',
  `complaint_content` text COMMENT '客诉内容',
  `customer_service_mid` bigint(20) NOT NULL DEFAULT '0' COMMENT '跟进客服mid',
  `operator_mid` bigint(20) NOT NULL DEFAULT '0' COMMENT '处理人mid',
  `first_response_time` datetime NOT NULL DEFAULT '1970-08-02 00:00:00' COMMENT '首响时间',
  `finish_time` datetime NOT NULL DEFAULT '1970-08-02 00:00:00' COMMENT '结案时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_mid` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建人mid',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_complaint_no` (`complaint_no`),
  KEY `idx_vin_sufix` (`vin_sufix`),
  KEY `idx_contact_phone_sufix` (`contact_phone_sufix`),
  KEY `idx_car_no` (`car_no`),
  KEY `idx_vid` (`vid`),
  KEY `idx_operator_mid` (`operator_mid`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_finish_time` (`finish_time`),
  KEY `idx_fir_res_time` (`first_response_time`),
  KEY `idx_md5` (`contact_phone_md5`)
) ENGINE=InnoDB AUTO_INCREMENT=7344 DEFAULT CHARSET=utf8mb4 COMMENT='客诉单'

CREATE TABLE `complaint_tag` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `complaint_no` varchar(255) NOT NULL DEFAULT '' COMMENT '客诉单号',
  `tag_type` varchar(32) NOT NULL DEFAULT '0' COMMENT '标签类型 1 投诉率免考核（COMPLAINT_RATE_ASSESSMENT_FREE） 2 72H结案免考核(FINISH_72H_ASSESSMENT_FREE) 3 首响超时(FIRST_RESPONSE_TIMEOUT) 4 结案超时(FINISH_TIMEOUT)',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除, 0-未删, 1-已删',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_complaint_no` (`complaint_no`)
) ENGINE=InnoDB AUTO_INCREMENT=8175 DEFAULT CHARSET=utf8mb4 COMMENT='客诉标签表'

CREATE TABLE `complaint_follow_process` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `complaint_no` varchar(255) NOT NULL DEFAULT '' COMMENT '客诉单号',
  `process_type` varchar(255) NOT NULL DEFAULT '0' COMMENT '跟进记录类型 1 跟进记录 2 申请信息 3 审批信息 4 维保单信息 5 积分信息 ...',
  `process_instance_id` varchar(255) NOT NULL DEFAULT '' COMMENT 'BPM流程实例id',
  `process_content` text COMMENT '记录内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_complaint_no` (`complaint_no`),
  KEY `idx_ct_time` (`create_time`),
  KEY `idx_pro_type` (`process_type`)
) ENGINE=InnoDB AUTO_INCREMENT=79490 DEFAULT CHARSET=utf8mb4 COMMENT='客诉单跟进记录'

CREATE TABLE `complaint_audit` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `complaint_no` varchar(20) NOT NULL DEFAULT '' COMMENT '客诉单号',
  `audit_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '审批单类型 1 申请改派门店 2 申请72H无法结案 3 申请免责 4 申请结案',
  `audit_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '审核状态 0 默认 1 待审核 2 通过 3 驳回',
  `vid` varchar(20) NOT NULL DEFAULT '' COMMENT '车辆vid',
  `car_no` varchar(20) NOT NULL DEFAULT '' COMMENT '车牌号',
  `org_id` varchar(32) NOT NULL DEFAULT '' COMMENT '门店id',
  `org_name` varchar(255) NOT NULL DEFAULT '' COMMENT '门店名称',
  `zone_id` varchar(32) NOT NULL DEFAULT '' COMMENT '大区ID',
  `little_zone_id` varchar(32) NOT NULL DEFAULT '' COMMENT '汽车小区id',
  `contact_name_c` varchar(255) NOT NULL DEFAULT '' COMMENT '联系人姓名密文',
  `contact_phone_c` varchar(255) NOT NULL DEFAULT '' COMMENT '联系人电话密文',
  `contact_phone_md5` varchar(32) NOT NULL DEFAULT '' COMMENT '联系人电话md5',
  `apply_content` text COMMENT '申请内容, json格式,包括发起请求的参数',
  `audit_mid` bigint(20) NOT NULL DEFAULT '0' COMMENT '审批人mid，审批回调写入',
  `create_mid` bigint(20) NOT NULL DEFAULT '0' COMMENT '申请人mid',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `audit_comment` varchar(510) NOT NULL DEFAULT '' COMMENT '审批意见，也等价于驳回原因，纯字符串',
  PRIMARY KEY (`id`),
  KEY `idx_complaint_no` (`complaint_no`),
  KEY `idx_audit_mid` (`audit_mid`),
  KEY `idx_vid` (`vid`),
  KEY `idx_car_no` (`car_no`),
  KEY `idx_ct_time` (`create_time`),
  KEY `idx_md5` (`contact_phone_md5`)
) ENGINE=InnoDB AUTO_INCREMENT=7619 DEFAULT CHARSET=utf8mb4 COMMENT='客诉审批表'


CREATE TABLE `complaint_relation_order` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `complaint_no` varchar(20) NOT NULL DEFAULT '' COMMENT '客诉单号',
  `biz_no` varchar(20) NOT NULL DEFAULT '' COMMENT '业务单号',
  `biz_type` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '单据类型 1 维保单',
  `biz_extend_info` text COMMENT '业务单扩展信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_mr_biz_type` (`complaint_no`,`biz_no`,`biz_type`)
) ENGINE=InnoDB AUTO_INCREMENT=352 DEFAULT CHARSET=utf8 COMMENT='客诉单关联单据表'


CREATE TABLE `complaint_relation_closing_tag` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `complaint_no` varchar(255) NOT NULL DEFAULT '' COMMENT '客诉单号',
  `closing_tag_id_link` varchar(255) NOT NULL DEFAULT '0' COMMENT '结案标签id链路,用/连接,例如 1/2/3',
  `closing_tag_name_link` varchar(255) NOT NULL DEFAULT '' COMMENT '结案标签名称链路,用/连接,例如 汽车/一般投诉',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除, 0-未删, 1-已删',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_complaint_no` (`complaint_no`)
) ENGINE=InnoDB AUTO_INCREMENT=14116 DEFAULT CHARSET=utf8mb4 COMMENT='客诉单与结案标签关联表'

CREATE TABLE `user_complaint_expand` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `uc_no` varchar(20) NOT NULL DEFAULT '' COMMENT '客诉类单号',
  `reminder_times` int(11) NOT NULL DEFAULT '0' COMMENT '催单次数',
  `city_id` int(10) NOT NULL DEFAULT '0' COMMENT '城市id',
  `zone_id` int(10) NOT NULL DEFAULT '0' COMMENT '大区id',
  `little_zone_id` int(10) NOT NULL DEFAULT '0' COMMENT '小区id',
  `service_scene` varchar(255) NOT NULL DEFAULT '' COMMENT '举报场景：用,分隔',
  `contact_phone_suffix` int(10) NOT NULL DEFAULT '0' COMMENT '手机号后4位',
  `contact_phone_md5` varchar(32) NOT NULL DEFAULT '' COMMENT '联系人电话md5',
  `vin_suffix` varchar(10) NOT NULL DEFAULT '' COMMENT 'vin后6位',
  `judge_type` tinyint(10) NOT NULL DEFAULT '0' COMMENT '判定结果：0-未判定 1-有效 2-无效',
  `car_no` varchar(20) NOT NULL DEFAULT '' COMMENT '车牌号',
  `contact_gender` tinyint(4) NOT NULL DEFAULT '0' COMMENT '联系人性别 0 默认 1 男 2 女',
  `customer_service_mid` bigint(20) NOT NULL DEFAULT '0' COMMENT '跟进客服mid',
  `expand` text COMMENT '扩展字段',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uc_no` (`uc_no`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COMMENT='客诉类单据扩展表'



ALTER TABLE complaint_audit 
ADD COLUMN user_agreement TINYINT(4) NOT NULL DEFAULT 1 COMMENT '是否与用户达成一致 0-否 1-是',
ADD COLUMN vehicle_repaired TINYINT(4) NOT NULL DEFAULT 2 COMMENT '车辆异常是否修复 0-否 1-是 2-不涉及';

ALTER TABLE complaint_order ADD INDEX idx_o (org_id, `status`);