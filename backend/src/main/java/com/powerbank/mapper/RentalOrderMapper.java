package com.powerbank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powerbank.entity.RentalOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RentalOrderMapper extends BaseMapper<RentalOrder> {

    @Select("SELECT o.*, pb.code as power_bank_code, pb.hourly_rate, u.username, u.real_name " +
            "FROM rental_order o " +
            "LEFT JOIN power_bank pb ON o.power_bank_id = pb.id " +
            "LEFT JOIN sys_user u ON o.user_id = u.id " +
            "WHERE o.user_id = #{userId} ORDER BY o.created_at DESC")
    List<RentalOrder> selectOrdersByUserId(@Param("userId") Long userId);

    @Select("SELECT o.*, pb.code as power_bank_code, pb.hourly_rate, u.username, u.real_name " +
            "FROM rental_order o " +
            "LEFT JOIN power_bank pb ON o.power_bank_id = pb.id " +
            "LEFT JOIN sys_user u ON o.user_id = u.id " +
            "WHERE o.user_id = #{userId} AND o.status = 0 ORDER BY o.created_at DESC")
    List<RentalOrder> selectCurrentOrders(@Param("userId") Long userId);

    @Select("SELECT o.*, pb.code as power_bank_code, pb.hourly_rate, u.username, u.real_name " +
            "FROM rental_order o " +
            "LEFT JOIN power_bank pb ON o.power_bank_id = pb.id " +
            "LEFT JOIN sys_user u ON o.user_id = u.id " +
            "ORDER BY o.created_at DESC")
    IPage<RentalOrder> selectAllOrders(Page<RentalOrder> page);

    @Select("SELECT o.*, pb.code as power_bank_code, pb.hourly_rate, u.username, u.real_name " +
            "FROM rental_order o " +
            "LEFT JOIN power_bank pb ON o.power_bank_id = pb.id " +
            "LEFT JOIN sys_user u ON o.user_id = u.id " +
            "WHERE u.id = #{userId} ORDER BY o.created_at DESC")
    List<RentalOrder> searchByUserId(@Param("userId") Long userId);
}
