/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved.
 * Author: lipengxiang
 * Date: 2020-2020/6/5 15:24
 * Desc:
 */
package cn.brainpoint.febs.identify.dao;

import org.apache.ibatis.session.SqlSession;

import cn.brainpoint.febs.identify.Identify;

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
        finally {
            session.close();
        }
    }

    /**
     * Get a new machine id.
     * @return
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
        finally {
            session.close();
        }

        return id;
    }
}
