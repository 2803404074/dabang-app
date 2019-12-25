package com.dbvr.baselibrary.model;

import java.util.List;

public class ProvinceMo {
    private int id;
    private String name;
    private boolean isCheck;
    private List<CityMo> districts;

    public ProvinceMo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public ProvinceMo(String name, List<CityMo> districts) {
        this.name = name;
        this.districts = districts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityMo> getDistricts() {
        return districts;
    }

    public void setDistricts(List<CityMo> districts) {
        this.districts = districts;
    }

    public static class CityMo{
        private int id;
        private String name;
        private boolean isCheck;
        private List<XianMo> districts;

        public CityMo() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<XianMo> getDistricts() {
            return districts;
        }

        public void setDistricts(List<XianMo> districts) {
            this.districts = districts;
        }
    }

    public static class XianMo{
        private int id;
        private String name;
        private boolean isCheck;

        public XianMo() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
