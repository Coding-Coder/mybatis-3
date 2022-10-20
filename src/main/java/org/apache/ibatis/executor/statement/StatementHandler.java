/*
 *    Copyright 2009-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.executor.statement;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 语句处理器
 *
 * @author Clinton Begin
 */
public interface StatementHandler {

  //基于JDBC连接来声明创建Statement
  Statement prepare(Connection connection, Integer transactionTimeout)
      throws SQLException;

  //参数化处理，为Statement 设置参数
  void parameterize(Statement statement)
      throws SQLException;

  //添加批处理（并非执行）
  void batch(Statement statement)
      throws SQLException;

  //执行update操作
  int update(Statement statement)
      throws SQLException;

  //执行query操作-->结果给ResultHandler
  <E> List<E> query(Statement statement, ResultHandler resultHandler)
      throws SQLException;

  <E> Cursor<E> queryCursor(Statement statement)
      throws SQLException;

  //得到绑定sql
  BoundSql getBoundSql();

  //得到参数处理器
  ParameterHandler getParameterHandler();

}
