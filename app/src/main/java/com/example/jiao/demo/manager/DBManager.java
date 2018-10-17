package com.example.jiao.demo.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jiao.demo.dao.CitySiteModelDao;
import com.example.jiao.demo.dao.DaoMaster;
import com.example.jiao.demo.dao.DaoSession;
import com.example.jiao.demo.dao.PhotoModelDao;
import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.database.Database;


/**
 * Created by zhjj on 2016/11/18.
 */
public class DBManager {

    private static DBManager instance;
    private String dbName = "demo.db";
    private DaoMaster.OpenHelper dbHelper;
    private DaoSession writDao;
    private String passwd = "abcdef";

    private DBManager(Context context){
//        dbHelper = new DaoMaster.DevOpenHelper(context, dbName);
        dbHelper = new MySQLiteOpenHelper(context.getApplicationContext(),dbName);
    }

    public static DBManager getInstance(Context context){
        if(instance == null){
            synchronized (DBManager.class){
                if(instance == null){
                    instance = new DBManager(context);
                }
            }
        }
        return instance;
    }

    public DaoSession getReadDao(){
        DaoMaster master = new DaoMaster(dbHelper.getReadableDatabase());
        return master.newSession();
    }

    public DaoSession getWritDao(){
        DaoMaster master = new DaoMaster(dbHelper.getWritableDatabase());
        return master.newSession();
    }

    public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {

        private Class[] dataModelClass = new Class[]{
                CitySiteModelDao.class,
                PhotoModelDao.class,
        };

        public MySQLiteOpenHelper(Context context, String name) {
            super(context, name);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                MigrationHelper.migrate(db,dataModelClass);
            }catch (Exception e){
                e.printStackTrace();
                Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            }catch (Error er){
                er.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            try {
                MigrationHelper.migrate(db,dataModelClass);
            }catch (Exception e){
                e.printStackTrace();
                Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            }catch (Error er){
                er.printStackTrace();
            }
        }
    }
}
