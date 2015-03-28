package ru.ifmo.thesis.gui.Util;


import javax.swing.*;

public class JValueField extends JTextField{
    public enum Type {
        DOUBLE,
        INT,
        UINT,
        PINT, //positive int
        PERCENTAGE //in [0,1]
    }
    private Type type;

    public JValueField(String value, JValueField.Type type, int columns){
        this.type = type;
        setText(value);
        setColumns(Math.abs(columns));
    }

    public boolean correctValue(){
        if (type == Type.DOUBLE){
            try{
                Double.parseDouble(getText());
                return true;
            }catch(Exception exc) {
                return false;
            }
        }
        if (type == Type.PERCENTAGE){
            try{
                double d = Double.parseDouble(getText());
                if (0<=d && d<=1){
                    return true;
                }else{
                    return false;
                }
            }catch(Exception exc) {
                return false;
            }
        }
        if (type == Type.INT){
            try{
                Integer.parseInt(getText());
                return true;
            }catch(Exception exc) {
                return false;
            }
        }
        if (type == Type.UINT){
            try{
                if (Integer.parseInt(getText()) >= 0) {
                    return true;
                }else{
                    return false;
                }
            }catch(Exception exc) {
                return false;
            }
        }
        if (type == Type.PINT){
            try{
                if (Integer.parseInt(getText()) > 0) {
                    return true;
                }else{
                    return false;
                }
            }catch(Exception exc) {
                return false;
            }
        }
        return false;
    }
}

