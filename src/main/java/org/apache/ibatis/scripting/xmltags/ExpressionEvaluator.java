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
package org.apache.ibatis.scripting.xmltags;

import org.apache.ibatis.builder.BuilderException;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 表达式求值器
 * 可参考ExpressionEvaluatorTest
 *
 * @author Clinton Begin
 */
public class ExpressionEvaluator {

  //表达式求布尔值，比如username == 'lxy'
  public boolean evaluateBoolean(String expression, Object parameterObject) {
	//非常简单，就是调用ognl
    Object value = OgnlCache.getValue(expression, parameterObject);
    if (value instanceof Boolean) {
      //如果是Boolean
      return (Boolean) value;
    }
    if (value instanceof Number) {
        //如果是Number，判断不为0
      return new BigDecimal(String.valueOf(value)).compareTo(BigDecimal.ZERO) != 0;
    }
    //否则判断不为null
    return value != null;
  }

  /**
   * 解析表达式到一个Iterable,核心是ognl
   * @deprecated Since 3.5.9, use the {@link #evaluateIterable(String, Object, boolean)}.
   */
   @Deprecated
  public Iterable<?> evaluateIterable(String expression, Object parameterObject) {
    return evaluateIterable(expression, parameterObject, false);
  }

  /**
   * @since 3.5.9
   */
  public Iterable<?> evaluateIterable(String expression, Object parameterObject, boolean nullable) {
    //原生的ognl很强大，OgnlCache.getValue直接就可以返回一个Iterable型或数组型或Map型了
    Object value = OgnlCache.getValue(expression, parameterObject);
    if (value == null) {
      if (nullable) {
        return null;
      } else {
        throw new BuilderException("The expression '" + expression + "' evaluated to a null value.");
      }
    }
    if (value instanceof Iterable) {
      return (Iterable<?>) value;
    }
    if (value.getClass().isArray()) {
    	//如果是array，则把他变成一个List<Object>
    	//注释下面提到了，不能用Arrays.asList()，因为array可能是基本型，这样会出ClassCastException，
    	//见https://code.google.com/p/mybatis/issues/detail?id=209
      // the array may be primitive, so Arrays.asList() may throw
      // a ClassCastException (issue 209).  Do the work manually
      // Curse primitives! :) (JGB)
      int size = Array.getLength(value);
      List<Object> answer = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        Object o = Array.get(value, i);
        answer.add(o);
      }
      return answer;
    }
    if (value instanceof Map) {
      return ((Map) value).entrySet();
    }
    throw new BuilderException("Error evaluating expression '" + expression + "'.  Return value (" + value + ") was not iterable.");
  }

}
