/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved.
 * Author: lipengxiang
 * Date: 2020-2020/6/11 16:59
 * Desc:
 */
package cn.brainpoint.febs.identify;

import lombok.Data;


/**
 * store unique machine id in database.
 */
@Data
public class IdentifyCfg {

    protected IdentifyCfg() {}

    /**
     * @param type e.g. mysql
     * @param url e.g. localhost:3306/xx
     * @param username the username of database.
     * @param password the password.
     */
    public IdentifyCfg(String type, String url, String username, String password) {
        this.type = type;
        this.url = url;
        this.username = username;
        this.password = password;
        this.tablename = null;
        this.retryCount = 0;
        this.connectTimeout = 0;
    }
    /**
     * @param type e.g. mysql
     * @param url e.g. localhost:3306/xx
     * @param username the username of database.
     * @param password the password.
     * @param tablename where to store id.
     * @param retryCount retry to connect to database.
     * @param connectTimeout connect timeout in milliseconds.
     */
    public IdentifyCfg(String type, String url, String username, String password, String tablename, int retryCount, int connectTimeout) {
        this.type = type;
        this.url = url;
        this.username = username;
        this.password = password;
        this.tablename = tablename;
        this.retryCount = retryCount;
        this.connectTimeout = connectTimeout;
    }

    public String getDriver() {
        if (this.type.equals("mysql")) {
            return "com.mysql.cj.jdbc.Driver";
        }
        throw new RuntimeException("only support mysql");
    }

    public String getUri() {
        if (this.type.equals("mysql")) {
            return "jdbc:mysql://" + this.url;
        }
        throw new RuntimeException("only support mysql");
    }

    /** db type; e.g. mysql **/
    protected String type;
    /** db url; e.g. localhost:3306/xx **/
    protected String url;
    /** the username of database. **/
    protected String username;
    protected String password;
    /** the db table name. **/
    protected String tablename;
    /** retry to connect to database. **/
    protected int retryCount;
    /** connect timeout in milliseconds. **/
    protected int connectTimeout;
}