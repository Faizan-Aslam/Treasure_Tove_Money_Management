package com.mcapp.mcapp.ui;

import android.app.Application;

public class GlobalClass extends Application {
        private String filterValue = null;
        private String sortValue = null;


        public String getFilterValue() {

            return filterValue;
        }

        public void setFilterValue(String aFilterValue) {

            filterValue = aFilterValue;
        }

        public String getSortValue() {

            return sortValue;
        }

        public void setSortValue(String aSortValue) {

            sortValue = aSortValue;
        }

    }
