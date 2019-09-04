package com.mvc.custom.mapper;


import com.mvc.custom.annotation.Delete;
import com.mvc.custom.annotation.Insert;
import com.mvc.custom.annotation.Param;
import com.mvc.custom.annotation.Query;
import com.mvc.custom.annotation.Update;
import com.mvc.custom.entity.User;

public interface UserMapper {

    @Query("select * from user  where account = #{account} and password = #{password}")
    public User selectUser(@Param("account") String account,@Param("password")String password);
    @Insert("insert into user(account,password,nick,sex,age) values(#{account}, #{password}, #{nick}, #{sex}, #{age})")
    public int InsertUser(@Param("account") String account,@Param("password") String password,@Param("nick") String nick,
    		@Param("sex") String sex ,@Param("age") int age);
    @Update("update user set account=#{account},password=#{password},nick=#{nick},sex=#{sex},age=#{age} where id=#{id}")
    public int UpdateUser(@Param("id") int id,@Param("account") String account,@Param("password") String password,
    		@Param("nick") String nick,@Param("sex") String sex ,@Param("age") int age);
    @Delete("delete from user where id = #{id}")
    public int DeleteUser(@Param("id") int id);

}
