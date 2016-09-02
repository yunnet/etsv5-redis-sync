package com.etrans.etsv5.app.redis.sync;
/** 
 * 工程名称: etsv5-redis-cacher  <br />
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: Table.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年3月29日 下午2:05:15  <br />
 * 最后修改: 2016年3月29日 下午2:05:15  <br />
 * 修改历史:   <br />
 */
public class Table {
	/**
	 * 别名
	 */
	private String alias;
	
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 字段名
	 */
	private String fields;
	
	/**
	 * 查询条件
	 */
	private String condition;
	
	/**
	 * 构造函数
	 * @param _tableName 表名
	 */
	public Table(final String _tableName) {
		this.tableName = _tableName;
	}
	
	/**
	 * 获取别名是否为空
	 * @return
	 */
	public boolean isnullAlias(){
		return null == this.alias || this.alias.isEmpty();
	}
	
	/**
	 * 获取别名
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * 设置别名
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * 获取
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * 获取
	 * @return the fields
	 */
	public String getFields() {
		return fields;
	}

	/**
	 * 设置
	 * @param fields the fields to set
	 */
	public void setFields(final String fields) {
		this.fields = fields;
	}

	/**
	 * 获取
	 * @return the condition
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * 设置
	 * @param condition the condition to set
	 */
	public void setCondition(final String condition) {
		this.condition = condition;
	}

	/**
	 * 生成SQLText
	 * @return
	 */
	public String getSQLText(){
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		if(null != fields && !fields.isEmpty())
			sb.append(fields);
		else
			sb.append("*");
		sb.append(" FROM ").append(tableName);
		
		if(null != condition && !condition.isEmpty())
			sb.append(" WHERE ").append(condition);
		
		return sb.toString();
	}

}
