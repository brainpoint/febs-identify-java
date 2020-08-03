/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved.
 * Author: lipengxiang
 * Date: 2020-2020/6/5 14:06
 * Desc:
 */
package cn.brainpoint.febs.identify;

import cn.brainpoint.febs.identify.dao.BaseService;
import cn.brainpoint.febs.identify.dao.MachineIdService;
import cn.brainpoint.febs.identify.exception.DBException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Identify {
    /**
     * machine id.
     */
    private static int machineId = 0;
    private static final int ID_LENGTH = 20;
    private static final String DEFAULT_TABLENAME = "_distribute_machineId";

    public static class Configuration {
        private static String driver;
        private static String url;
        private static String username;
        private static String password;
        @Getter
        private static String tablename;
        private static int retryCount;
    }

    /**
     * Initialize with database configuration, and create a machine id.
     * 
     * @param machine_id use this machine_id to make distributed unique id.
     */
    public static void initializeByMachineId(int machine_id) {
        if ((machine_id & 0xff000000) != 0 || machine_id == 0) {
            throw new IllegalArgumentException(
                    "The machine identifier must be between 1 and 16777215 (it must fit in three bytes).");
        }

        machine_id %= 0xff000000;
        log.info(String.format("[febs] Machine ID: %s;", machine_id));

        machineId = machine_id;
    }

    /**
     * Initialize with database configuration, and create a machine id.
     * 
     * @param config db config.
     */
    public static void initializeByDatabase(IdentifyCfg config) {
        if (config != null) {
            setupDatabase(config);
        }

        try {
            machineId = generateNewMachineId();
        } catch (DBException e) {
            log.error(e.getMessage(), e);
            System.exit(-1);
        }

        log.info(String.format("[febs] Machine ID: %s; Url: %s; Table: %s", machineId, Configuration.url,
                Configuration.tablename));

        // BaseService.destroy();
    }

    /**
     * Setup database configuration, but don't create a machine_id. And then can
     * call generateNewMachineId().
     * 
     * @param config db config.
     */
    public static void setupDatabase(IdentifyCfg config) {

        if (config == null) {
            throw new IllegalArgumentException("config is null");
        }
        if (config.getDriver() == null || config.getUri() == null || config.getPassword() == null
                || config.getUsername() == null) {
            throw new IllegalArgumentException("cfg is error");
        }

        String tablename = config.getTablename();
        if (null != tablename) {
            tablename = tablename.trim();
        }

        String dbTablename = tablename == null || tablename.length() == 0 ? DEFAULT_TABLENAME : tablename;
        int connectTimeout = config.getConnectTimeout() <= 0 ? 5000 : config.getConnectTimeout();

        if (dbTablename.equals(Configuration.tablename) && config.getDriver().equals(Configuration.driver)
                && config.getUri().equals(Configuration.url) && config.getUsername().equals(Configuration.username)
                && config.getPassword().equals(Configuration.password)) {
            return;
        }

        Configuration.driver = config.getDriver();
        Configuration.url = config.getUri();
        Configuration.username = config.getUsername();
        Configuration.password = config.getPassword();
        Configuration.tablename = dbTablename;
        Configuration.retryCount = config.getRetryCount();

        BaseService.initialize(Configuration.driver, Configuration.url, Configuration.username, Configuration.password,
                connectTimeout);
    }

    /**
     * Get the current machine id.
     * 
     * @return machine id.
     */
    public static int getMachineId() {
        return machineId;
    }

    /**
     * Generate a new machine id.
     * 
     * @return a new machine id.
     */
    public static int generateNewMachineId() throws DBException {
        int machine_id = 0;
        int i = 0;
        int retryCount = Configuration.retryCount;
        do {
            try {
                MachineIdService service = new MachineIdService();
                machine_id = service.getNewMachineId().intValue();
                break;
            } catch (Exception e) {
                if (retryCount <= 0) {
                    throw new DBException("Get Machine id error", e);
                }
                log.warn("Get Machine id error", e);
                log.info(String.format("[febs-identify] retry connect %d", ++i));
            }
        } while (--retryCount >= 0);

        return machine_id %= 0xff000000;
    }

    /**
     * Generate a new unique id (21size)
     * 
     * @return distributed unique id
     */
    public static String nextId() {
        String id = ObjectId.generateHexNoPID(machineId);

        assert id.length() == ObjectId.OBJECT_ID_LENGTH_NOPID
                * 2 : "generate objectID length != OBJECT_ID_LENGTH_noPID*2";

        return id;
    }

    /**
     * Validator id.
     * 
     * @param ids id array.
     * @return whether ids is valid.
     */
    public static boolean isValid(final String... ids) {
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            if (null == id || id.length() != ID_LENGTH) {
                return false;
            }

            for (int j = 0; j < id.length(); j++) {
                char c = id.charAt(j);
                if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))) {
                    return false;
                }
            }
        }

        return true;
    }
}
