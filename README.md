# etsv5-redis-sync

本程序是由事件和定时驱动，根据客户端传送的table, fields, condition字段，生成查询语句，从mysql中查询相应的记录，然后同步到redis中

#####  客户端
* 请求结构
| 字段名 | 类型 | 说明
|:-------:|:-------:|:-------:|
| genTime       | long         | 产生时间 |
| getObjectType | ObjCacheType | 对象类型 |
| requestID     | int          | 请求ID   |
| userID        | int          | 用户编号 |
| tableName     | String       | 表名     |
| fields        | String       | 字段名   |
| condition     | String       | 查询条件 |


#####  服务端
