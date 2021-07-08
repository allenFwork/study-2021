/*
 *    Copyright ${license.git.copyrightYears} the original author or authors.
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
package com.study.mapper;

import com.study.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.type.Alias;

import java.util.List;
import java.util.Map;

//@Alias("userMapper")
//@Mapper
public interface UserMapper {

  public List<Map<String, Object>> selectAll();

  // jdk8 之前没法获取方法的参数名，例如这里的map只能获取到arg0
  public User selectByMap(Map map);

  public User selectById(int id);

  public User findUserByUsername(String userName);

//  public User findByNameAndPower(@Param("name") String name, @Param("power") String power);
  public User findByNameAndPower(String name, String power);

  public User findUserByIdAndUsername(int id, @Param("name") String userName);

  public int updateName(User user);

}
