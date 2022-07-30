package com.example.topocompanion;

public class DataDb {
    private String Code;
    private String Localisation;
    private String qrUri;

    public DataDb(String Code, String Localisation, String qrUri){
        this.Code = Code;
        this.Localisation = Localisation;
        this.qrUri = qrUri;
    }

    public DataDb(){
    }

    public String getCode(){
        return Code;
    }

    public void setCode(String Code){
        this.Code = Code;
    }

    public String getLocalisation(){
        return Localisation;
    }

    public void setLocalisation (String Localisation){
        this.Localisation = Localisation;
    }

    public String getQrUri(){ return qrUri; }

    public void setQrUri(String qrUri){ this.qrUri = qrUri;}

    public String toString(){
        return this.Localisation;
    }
}
