/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved.
 * Author: lipengxiang
 * Date: 2020-2020/6/8 13:57
 * Desc:
 */
package cn.brainpoint.febs.identify.dao;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

/**
 *
 *
 * @author pengxiang.li
 * @date 2020/6/8 1:57 下午
 */
public class BaseService {

    private static SqlSessionFactory sqlSessionFactory;

    /**
     * Initialize the database.
     *
     * @param driver e.g. com.mysql.jdbc.Driver
     * @param url e.g. jdbc:mysql://localhost:3306/xx
     * @param username the username of database.
     * @param password the password.
     * @param timeoutInMilliseconds network timeout.
     */
    public static void initialize(String driver, String url, String username, String password, int timeoutInMilliseconds) {
        PooledDataSource db = new PooledDataSource(driver, url, username, password);
        db.setDefaultNetworkTimeout(timeoutInMilliseconds);

        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("machineId", transactionFactory, db);
        Configuration config = new Configuration(environment);
        config.setMapUnderscoreToCamelCase(true);

        config.addMapper(IMachineIdMapperMysql.class);

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
    }

    /**
     * destroy the database.
     */
    public static void destroy() {
        sqlSessionFactory = null;
    }

    /**
     * Open a session.
     * @return sqlSession
     */
    public static SqlSession openSession() {
        return sqlSessionFactory.openSession();
    }
}
