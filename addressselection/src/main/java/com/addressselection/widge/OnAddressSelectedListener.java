package com.addressselection.widge;


import com.addressselection.bean.City;
import com.addressselection.bean.County;
import com.addressselection.bean.Province;
import com.addressselection.bean.Street;

public interface OnAddressSelectedListener {
    void onAddressSelected(Province province, City city, County county, Street street);
}
