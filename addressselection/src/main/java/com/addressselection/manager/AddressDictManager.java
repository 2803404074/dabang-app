package com.addressselection.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.FileUtils;
import android.util.Log;
import android.widget.Toast;


import com.addressselection.bean.AddressBEAN;
import com.addressselection.bean.AdressBean;
import com.addressselection.bean.AdressBean_two;
import com.addressselection.bean.City;
import com.addressselection.bean.County;
import com.addressselection.bean.Province;
import com.addressselection.bean.Street;
import com.addressselection.utils.fileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.addressselection.manager.TableField.ADDRESS_DICT_FIELD_CENTER;
import static com.addressselection.manager.TableField.ADDRESS_DICT_FIELD_CODE;
import static com.addressselection.manager.TableField.ADDRESS_DICT_FIELD_ID;
import static com.addressselection.manager.TableField.ADDRESS_DICT_FIELD_PARENTID;


/**
 * Created by huanghaojie on 2016/10/19.
 * 对地址库的增删改查
 */

public class AddressDictManager {
    private static final String TAG = "AddressDictManager";
    private final String SqlFile = "address.db";
    ;
    private SQLiteDatabase db;
    private AddressBEAN addressBeans;
    private List<AdressBean_two> updateList;

    public AddressDictManager(Context context, boolean updateTab, List<AdressBean_two> list) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(context);
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        updateList = list;
//        db = DBUtils.getDBInstance(context);
        if (updateTab) {
            String databaseFilepath = mg.getDatabaseFile(SqlFile);
            File file = new File(databaseFilepath);

            if (file.exists()) {
                db = mg.getDatabase(SqlFile);
                addAddress(false);
            } else {
                db = DBUtils.getDBInstance(context);
                addAddress(true);
            }
        } else {
            db = mg.getDatabase(SqlFile);
        }

    }


    public void addAddress(boolean updateTab) {
        if (updateList == null) {
            return;
        }
        List<AddressBEAN> addressBEANList = getAddressBEANList(updateList);
        for (int i = 0; i < addressBEANList.size(); i++) {
            AdressBean.ChangeRecordsBean changeRecordsBean = new AdressBean.ChangeRecordsBean();
            changeRecordsBean.code = addressBEANList.get(i).getAdcode();
            changeRecordsBean.id = addressBEANList.get(i).getAdcode();
            changeRecordsBean.name = addressBEANList.get(i).getName();
            changeRecordsBean.parentId = addressBEANList.get(i).getCitycode();
            changeRecordsBean.center = addressBEANList.get(i).getCenter();
            if (updateTab) {
                inserddress(changeRecordsBean);
            } else {
                updateAddressInfo(changeRecordsBean);
            }
        }

    }

    private List<AddressBEAN> getAddressBEANList(List<AdressBean_two> addressBeans_two) {

        List<AddressBEAN> list = new ArrayList<>();
        for (int j = 0; j < addressBeans_two.size(); j++) {
            addressBeans = new AddressBEAN();
            addressBeans.setName(addressBeans_two.get(j).getName());
            addressBeans.setAdcode(addressBeans_two.get(j).getAdcode());
            addressBeans.setCenter(addressBeans_two.get(j).getCenter());
            addressBeans.setCitycode("0");
            list.add(addressBeans);
            if (addressBeans_two.get(j).getDistricts() != null || addressBeans_two.get(j).getDistricts().size() > 0) {
                for (int i = 0; i < addressBeans_two.get(j).getDistricts().size(); i++) {
                    addressBeans = new AddressBEAN();
                    addressBeans.setName(addressBeans_two.get(j).getDistricts().get(i).getName());
                    addressBeans.setCitycode(addressBeans_two.get(j).getAdcode());
                    addressBeans.setCenter(addressBeans_two.get(j).getDistricts().get(i).getCenter());
                    addressBeans.setAdcode(addressBeans_two.get(j).getDistricts().get(i).getAdcode());
                    list.add(addressBeans);
                    if (addressBeans_two.get(j).getDistricts().get(i).getDistricts() != null || addressBeans_two.get(j).getDistricts().get(i).getDistricts().size() > 0) {
                        for (int k = 0; k < addressBeans_two.get(j).getDistricts().get(i).getDistricts().size(); k++) {
                            addressBeans = new AddressBEAN();
                            addressBeans.setName(addressBeans_two.get(j).getDistricts().get(i).getDistricts().get(k).getName());
                            addressBeans.setAdcode(addressBeans_two.get(j).getDistricts().get(i).getDistricts().get(k).getAdcode());
                            addressBeans.setCenter(addressBeans_two.get(j).getDistricts().get(i).getDistricts().get(k).getCenter());
                            addressBeans.setCitycode(addressBeans_two.get(j).getDistricts().get(i).getAdcode());
                            list.add(addressBeans);
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 增加一个地址
     *
     * @param adress
     */
    public void inserddress(AdressBean.ChangeRecordsBean adress) {
        if (adress != null) {
            db.beginTransaction();//手动设置开启事务
            try {
                ContentValues values = new ContentValues();
                values.put(ADDRESS_DICT_FIELD_CODE, adress.code);
                values.put(TableField.ADDRESS_DICT_FIELD_NAME, adress.name);
                values.put(ADDRESS_DICT_FIELD_PARENTID, adress.parentId);
                values.put(ADDRESS_DICT_FIELD_ID, adress.id);
                values.put(ADDRESS_DICT_FIELD_CENTER, adress.center);
                db.insert(TableField.TABLE_ADDRESS_DICT, null, values);
                db.setTransactionSuccessful(); //设置事务处理成功
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction(); //事务终止
            }
        }
    }

    /**
     * 增加地址集合
     *
     * @param list
     */
    public void insertAddress(List<AdressBean.ChangeRecordsBean> list) {
        if (list != null) {
            db.beginTransaction();//手动设置开启事务
            try {
                for (AdressBean.ChangeRecordsBean adress : list) {
                    ContentValues values = new ContentValues();
                    values.put(ADDRESS_DICT_FIELD_CODE, adress.code);
                    values.put(TableField.ADDRESS_DICT_FIELD_NAME, adress.name);
                    values.put(ADDRESS_DICT_FIELD_PARENTID, adress.parentId);
                    values.put(ADDRESS_DICT_FIELD_ID, adress.id);
                    db.insert(TableField.TABLE_ADDRESS_DICT, null, values);
                }
                db.setTransactionSuccessful(); //设置事务处理成功
            } catch (Exception e) {
            } finally {
                db.endTransaction(); //事务终止
            }
        }
    }

    //更新地址
    public void updateAddressInfo(AdressBean.ChangeRecordsBean adress) {
        if (adress != null) {
            db.beginTransaction();//手动设置开启事务
            try {
                ContentValues values = new ContentValues();
                values.put(ADDRESS_DICT_FIELD_CODE, adress.code);
                values.put(TableField.ADDRESS_DICT_FIELD_NAME, adress.name);
                values.put(ADDRESS_DICT_FIELD_PARENTID, adress.parentId);
                values.put(ADDRESS_DICT_FIELD_ID, adress.id);
                values.put(ADDRESS_DICT_FIELD_CENTER, adress.center);
                String[] args = {String.valueOf(adress.id)};
                db.update(TableField.TABLE_ADDRESS_DICT, values, TableField.FIELD_ID + "=?", args);
                db.setTransactionSuccessful(); //设置事务处理成功
            } catch (Exception e) {
            } finally {
                db.endTransaction(); //事务终止
            }
        }
    }

    /**
     * 查找 地址 数据
     *
     * @return
     */
    public List<AdressBean.ChangeRecordsBean> getAddressList() {
        List<AdressBean.ChangeRecordsBean> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " order by sort asc", null);
        while (cursor.moveToNext()) {
            AdressBean.ChangeRecordsBean adressInfo = new AdressBean.ChangeRecordsBean();
            adressInfo.id = cursor.getString(cursor.getColumnIndex(TableField.FIELD_ID));
            adressInfo.parentId = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_PARENTID));
            adressInfo.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            adressInfo.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            list.add(adressInfo);
        }
        cursor.close();
        return list;
    }

    /**
     * 获取省份列表
     *
     * @return
     */
    public List<Province> getProvinceList() {
        List<Province> provinceList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " where " + ADDRESS_DICT_FIELD_PARENTID + "=?", new String[]{String.valueOf(0)});
        while (cursor.moveToNext()) {
            Province province = new Province();
            province.id = cursor.getInt(cursor.getColumnIndex(ADDRESS_DICT_FIELD_ID));
            province.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            province.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            province.center = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CENTER));
            provinceList.add(province);
        }
        cursor.close();

        return provinceList;
    }

    /**
     * 获取省份
     *
     * @param provinceCode
     * @return
     */
    public String getProvince(String provinceCode) {
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " where " + ADDRESS_DICT_FIELD_CODE + "=?", new String[]{provinceCode});
        if (cursor != null && cursor.moveToFirst()) {
            Province province = new Province();
            province.id = cursor.getInt(cursor.getColumnIndex(ADDRESS_DICT_FIELD_ID));
            province.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            province.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            province.center = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CENTER));
            cursor.close();
            return province.name;
        } else {
            return "";
        }
    }

    /**
     * 获取省份
     *
     * @param provinceCode
     * @return
     */
    public Province getProvinceBean(String provinceCode) {
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " where " + ADDRESS_DICT_FIELD_CODE + "=?", new String[]{provinceCode});
        if (cursor != null && cursor.moveToFirst()) {
            Province province = new Province();
            province.id = cursor.getInt(cursor.getColumnIndex(ADDRESS_DICT_FIELD_ID));
            province.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            province.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            province.center = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CENTER));
            cursor.close();
            return province;
        } else {
            return null;
        }
    }

    /**
     * 获取省份对应的城市列表
     *
     * @return
     */
    public List<City> getCityList(int provinceId) {
        List<City> cityList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " where " + ADDRESS_DICT_FIELD_PARENTID + "=?", new String[]{String.valueOf(provinceId)});
        while (cursor.moveToNext()) {
            City city = new City();
            city.id = cursor.getInt(cursor.getColumnIndex(ADDRESS_DICT_FIELD_ID));
            city.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            city.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            city.center = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CENTER));
            cityList.add(city);
        }
        cursor.close();
        Log.d("luhuas", "getCityList: " + cityList.size());
        return cityList;
    }

    /**
     * 获取城市
     *
     * @return
     */
    public String getCity(String cityCode) {
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " where " + ADDRESS_DICT_FIELD_CODE + "=?", new String[]{cityCode});
        if (cursor != null && cursor.moveToFirst()) {
            City city = new City();
            city.id = cursor.getInt(cursor.getColumnIndex(ADDRESS_DICT_FIELD_ID));
            city.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            city.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            city.center = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CENTER));
            cursor.close();
            return city.name;
        } else {
            return "";
        }
    }

    /**
     * 获取城市
     *
     * @return
     */
    public City getCityBean(String cityCode) {
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " where " + ADDRESS_DICT_FIELD_CODE + "=?", new String[]{cityCode});
        if (cursor != null && cursor.moveToFirst()) {
            City city = new City();
            city.id = cursor.getInt(cursor.getColumnIndex(ADDRESS_DICT_FIELD_ID));
            city.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            city.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            city.center = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CENTER));
            cursor.close();
            return city;
        } else {
            return null;
        }
    }

    /**
     * 获取城市对应的区，乡镇列表
     *
     * @return
     */
    public List<County> getCountyList(int cityId) {
        List<County> countyList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " where " + ADDRESS_DICT_FIELD_PARENTID + "=?", new String[]{String.valueOf(cityId)});
        while (cursor.moveToNext()) {
            County county = new County();
            county.id = cursor.getInt(cursor.getColumnIndex(ADDRESS_DICT_FIELD_ID));
            county.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            county.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            county.center = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CENTER));
            countyList.add(county);
        }
        cursor.close();
        return countyList;
    }

    public String getCounty(String countyCode) {
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " where " + ADDRESS_DICT_FIELD_CODE + "=?", new String[]{countyCode});
        if (cursor != null && cursor.moveToFirst()) {
            County county = new County();
            county.id = cursor.getInt(cursor.getColumnIndex(ADDRESS_DICT_FIELD_ID));
            county.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            county.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            county.center = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CENTER));
            cursor.close();
            return county.name;
        } else {
            return "";
        }
    }

    public County getCountyBean(String countyCode) {
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " where " + ADDRESS_DICT_FIELD_CODE + "=?", new String[]{countyCode});
        if (cursor != null && cursor.moveToFirst()) {
            County county = new County();
            county.id = cursor.getInt(cursor.getColumnIndex(ADDRESS_DICT_FIELD_ID));
            county.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            county.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            county.center = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CENTER));
            cursor.close();
            return county;
        } else {
            return null;
        }
    }

    /**
     * 获取区，乡镇对应的街道列表
     *
     * @return
     */
    public List<Street> getStreetList(int countyId) {
        List<Street> streetList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " where " + ADDRESS_DICT_FIELD_PARENTID + "=?", new String[]{String.valueOf(countyId)});
        while (cursor.moveToNext()) {
            Street street = new Street();
            street.id = cursor.getInt(cursor.getColumnIndex(ADDRESS_DICT_FIELD_ID));
            street.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            street.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            street.center = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CENTER));
            streetList.add(street);
        }
        cursor.close();
        return streetList;
    }

    public String getStreet(String streetCode) {
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " where " + ADDRESS_DICT_FIELD_CODE + "=?", new String[]{streetCode});
        if (cursor != null && cursor.moveToFirst()) {
            Street street = new Street();
            street.id = cursor.getInt(cursor.getColumnIndex(ADDRESS_DICT_FIELD_ID));
            street.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            street.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            street.center = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CENTER));
            cursor.close();
            return street.name;
        } else {
            return "";
        }
    }

    public Street getStreetBean(String streetCode) {
        Cursor cursor = db.rawQuery("select * from " + TableField.TABLE_ADDRESS_DICT + " where " + ADDRESS_DICT_FIELD_CODE + "=?", new String[]{streetCode});
        if (cursor != null && cursor.moveToFirst()) {
            Street street = new Street();
            street.id = cursor.getInt(cursor.getColumnIndex(ADDRESS_DICT_FIELD_ID));
            street.code = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CODE));
            street.name = cursor.getString(cursor.getColumnIndex(TableField.ADDRESS_DICT_FIELD_NAME));
            street.center = cursor.getString(cursor.getColumnIndex(ADDRESS_DICT_FIELD_CENTER));
            cursor.close();
            return street;
        } else {
            return null;
        }
    }

    /**
     * 查找消息临时列表中是否存在这一条记录
     *
     * @param bannerInfo banner数据
     * @return
     */
    public int isExist(AdressBean.ChangeRecordsBean bannerInfo) {
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from " + TableField.TABLE_ADDRESS_DICT + " where " + TableField.FIELD_ID + "=?", new String[]{String.valueOf(bannerInfo.id)});
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

}
