/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved.
 * Author: lipengxiang
 * Date: 2020-2020/6/8 10:51
 * Desc:
 */
package cn.brainpoint.febs.identify.dao;

import org.apache.ibatis.annotations.*;

/**
 *
 *
 * @author pengxiang.li
 * @date 2020/6/8 10:51 上午
 */
@Mapper
public interface IMachineIdMapperMysql {

    /**
     * Assure the table in database..
     * @param tablename
     */
    @Update("CREATE TABLE IF NOT EXISTS `${tablename}`(`id` bigint UNSIGNED NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`)) " +
            "COLLATE='utf8_general_ci' " +
            "ENGINE=InnoDB;")
    void assureTable(@Param("tablename") String tablename);

    /**
     * Get new machine id.
     * @param tablename
     * @return
     */
    @Insert("INSERT INTO `${tablename}`() VALUES()")
    @Options(useGeneratedKeys = true, keyProperty = "mod.id")
    void getNewId(@Param("tablename") String tablename, @Param("mod") MachineIdBean mod);
}
