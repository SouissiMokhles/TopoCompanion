package com.example.topocompanion;

public class MissionDb {
    String codeMission,client,gouv,sup,Date,MarkerId;

    double lat,lng;

    public MissionDb (String codeMission, String client,String gouv, Double lat, Double lng, String sup, String Date, String MarkerId){
        this.codeMission = codeMission;
        this.client = client;
        this.gouv = gouv;
        this.Date = Date;
        this.sup = sup;
        this.lat = lat;
        this.lng = lng;
        this.MarkerId = MarkerId;
    }

    public String getGouv(){return  gouv;}

    public void setGouv(String gouv) {this.gouv = gouv;}

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getSup() {
        return sup;
    }

    public void setSup(String sup) {
        this.sup = sup;
    }

    public String getCodeMission(){
        return codeMission;
    }

    public void setCodeMission(String codeMission) {
        this.codeMission = codeMission;
    }

    public String getClient(){
        return client;
    }

    public void setClient(String client){
        this.client=client;
    }

    public Double getLat(){return lat;}

    public void setLat(Double lat){this.lat = lat;}

    public Double getLng(){return lng;}

    public void setLng(double lng) {this.lng = lng;}

    public String getMarkerId() {
        return MarkerId;
    }

    public void setMarkerId(){
        this.MarkerId = MarkerId;
    }

    public void setMarkerId(String markerId) {
        MarkerId = markerId;
    }

    public MissionDb(){}

}
