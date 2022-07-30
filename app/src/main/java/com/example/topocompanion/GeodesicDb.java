package com.example.topocompanion;

public class GeodesicDb {
    String codePoint, gouv;
    double x,y,z;

    public GeodesicDb (String codePoint, String gouv, double x, double y, double z){
        this.codePoint = codePoint;
        this.gouv = gouv;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getCodePoint() {
        return codePoint;
    }

    public String getGouv() {
        return gouv;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setCodePoint(String codePoint) {
        this.codePoint = codePoint;
    }

    public void setGouv(String gouv) {
        this.gouv = gouv;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public GeodesicDb(){}
}
