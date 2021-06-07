package com.mcapp.mcapp.ui;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GlobalClass extends Application {
        private String userId = null;
        private String filterValue = null;
        private String sortValue = null;

        private FirebaseUser firebaseUser;

        public  String getUserId(){
            return userId;
        }
        public void setUserId(String aUserId){
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userId = firebaseUser.getUid();
        }
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
