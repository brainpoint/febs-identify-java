/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved.
 * Author: lipengxiang
 * Date: 2020-2020/6/30 15:31
 * Desc:
 */
package cn.brainpoint.febs.identify.exception;

/**
 * @author pengxiang.li
 * @date 2020/6/30 3:31 下午
 */
public class DBException extends RuntimeException {
    private static final long serialVersionUID = 6650258781086723349L;

    public DBException() {
        super();
    }

    public DBException(String message) {
        super(message);
    }

    public DBException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBException(Throwable cause) {
        super(cause);
    }
}
