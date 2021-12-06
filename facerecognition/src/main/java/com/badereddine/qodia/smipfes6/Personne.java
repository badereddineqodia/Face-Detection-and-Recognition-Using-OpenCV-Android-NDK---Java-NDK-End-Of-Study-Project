package com.badereddine.qodia.smipfes6;

public class Personne {
    String idPersonne;
    String namePersonne;
    String imagePathPersonne;
    String timePersonne;
    String portion;

    public Personne(String idPersonne, String namePersonne, String imagePathPersonne, String timePersonne, String portion) {
        this.idPersonne = idPersonne;
        this.namePersonne = namePersonne;
        this.imagePathPersonne = imagePathPersonne;
        this.timePersonne = timePersonne;
        this.portion = portion;
    }

    public String getIdPersonne() {
        return idPersonne;
    }

    public void setIdPersonne(String idPersonne) {
        this.idPersonne = idPersonne;
    }

    public String getNamePersonne() {
        return namePersonne;
    }

    public void setNamePersonne(String namePersonne) {
        this.namePersonne = namePersonne;
    }

    public String getImagePathPersonne() {
        return imagePathPersonne;
    }

    public void setImagePathPersonne(String imagePathPersonne) {
        this.imagePathPersonne = imagePathPersonne;
    }

    public String getTimePersonne() {
        return timePersonne;
    }

    public void setTimePersonne(String timePersonne) {
        this.timePersonne = timePersonne;
    }

    public String getPortion() {
        return portion;
    }

    public void setPortion(String portion) {
        this.portion = portion;
    }

    public Personne(){

    }
}
