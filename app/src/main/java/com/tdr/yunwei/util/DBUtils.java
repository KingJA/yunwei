package com.tdr.yunwei.util;

import android.os.Environment;
import android.util.Log;


import com.tdr.yunwei.bean.BeanID;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2016/12/23.
 */

public class DBUtils {
    private static DbManager.DaoConfig daoConfig;

    public static DbManager.DaoConfig getDb() {
//        Environment.getExternalStorageDirectory().getAbsolutePath()
        //本地数据的初始化
        if (daoConfig == null) {
            daoConfig = new DbManager.DaoConfig()
                    .setDbName("yunwei2.db") //设置数据库名
//                .setDbDir(new File(Environment.getExternalStorageDirectory().getAbsolutePath()))
                    .setDbVersion(2) //设置数据库版本,每次启动应用时将会检查该版本号,
                    .setTableCreateListener(new DbManager.TableCreateListener() {
                        @Override
                        public void onTableCreated(DbManager db, TableEntity<?> table) {

                            Log.e("PZX", "-------------创建数据库------------");
//                        db.getDatabase().enableWriteAheadLogging();
                        }
                    })//设置数据库创建时的Listener
                    .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                            Log.e("PZX", "-------------数据库升级------------");
                        }
                    });//设置数据库升级时的Listener,这里可以执行相关数据库表的相关修改,比如alter语句增加字段等
            //.setDbDir(null);//设置数据库.db文件存放的目录,默认为包名下databases目录下
        }
        return daoConfig;
    }

    public static List<?> SetListID(DbManager db, List<?> list, Class clas) throws DbException {
        List<BeanID> l2 = db.findAll(clas);
        int index = 0;
        if (l2 != null && l2.size() > 0) {
            for (BeanID CB : l2) {
                if (CB.getID() > index) {
                    index = CB.getID();
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            ((BeanID) list.get(i)).setID(++index);
        }
        return list;
    }

    public static void deletedb() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("yunwei2.db")
                .setDbVersion(1)
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {
                        LOG.E("-------------删除老数据库------------");

                    }
                });
        DbManager DB= x.getDb(daoConfig);
        try {
            DB.dropDb();
        } catch (DbException e) {
            LOG.E("删除老数据库异常="+e.toString());
        }
    }

}
