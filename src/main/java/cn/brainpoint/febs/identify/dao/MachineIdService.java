/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved.
 * Author: lipengxiang
 * Date: 2020-2020/6/5 15:24
 * Desc:
 */
package cn.brainpoint.febs.identify.dao;

import org.apache.ibatis.session.SqlSession;

import cn.brainpoint.febs.identify.Identify;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MachineIdService extends BaseService {

    public MachineIdService() {
        super();

        this.assureTable();
    }

    private void assureTable() {
        SqlSession session = openSession();
        try {
            IMachineIdMapperMysql mapper = session.getMapper(IMachineIdMapperMysql.class);
            mapper.assureTable(Identify.Configuration.getTablename());
            session.commit();
        }
        catch (Exception e) {
            log.error("[febs identity] db connect in assureTable: " + e.getMessage());
        }
        finally {
            session.close();
        }
    }

    /**
     * Get a new machine id.
     * @return machine id
     */
    public Long getNewMachineId() {
        SqlSession session = openSession();
        Long id = null;
        try {
            IMachineIdMapperMysql mapper = session.getMapper(IMachineIdMapperMysql.class);
            MachineIdBean mod = new MachineIdBean();
            mapper.getNewId(Identify.Configuration.getTablename(), mod);
            session.commit();

            id = mod.getId();
        }
        catch (Exception e) {
            log.error("[febs identity] db connect in getNewMachineId: " + e.getMessage());
        }
        finally {
            session.close();
        }

        return id;
    }
}
