package com.Wsdl2Code.WebServices.MAService;

//------------------------------------------------------------------------------
// <wsdl2code-generated>
//    This code was generated by http://www.wsdl2code.com version  2.6
//
// Date Of Creation: 5/15/2016 6:42:16 PM
//    Please dont change this code, regeneration will override your changes
//</wsdl2code-generated>
//
//------------------------------------------------------------------------------
//
//This source code was auto-generated by Wsdl2Code  Version
//
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import java.util.Hashtable;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

public class Alert implements KvmSerializable {
    
    public String alertTitle;
    public String alertMessage;
    public int alertInteger;
    public String alertURL;
    public String alertPhoto;
    public boolean isActive;
    
    public Alert(){}
    
    public Alert(SoapObject soapObject)
    {
        if (soapObject == null)
            return;
        if (soapObject.hasProperty("alertTitle"))
        {
            Object obj = soapObject.getProperty("alertTitle");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                alertTitle = j.toString();
            }else if (obj!= null && obj instanceof String){
                alertTitle = (String) obj;
            }
        }
        if (soapObject.hasProperty("alertMessage"))
        {
            Object obj = soapObject.getProperty("alertMessage");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                alertMessage = j.toString();
            }else if (obj!= null && obj instanceof String){
                alertMessage = (String) obj;
            }
        }
        if (soapObject.hasProperty("alertInteger"))
        {
            Object obj = soapObject.getProperty("alertInteger");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                alertInteger = Integer.parseInt(j.toString());
            }else if (obj!= null && obj instanceof Number){
                alertInteger = (Integer) obj;
            }
        }
        if (soapObject.hasProperty("alertURL"))
        {
            Object obj = soapObject.getProperty("alertURL");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                alertURL = j.toString();
            }else if (obj!= null && obj instanceof String){
                alertURL = (String) obj;
            }
        }
        if (soapObject.hasProperty("alertPhoto"))
        {
            Object obj = soapObject.getProperty("alertPhoto");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                alertPhoto = j.toString();
            }else if (obj!= null && obj instanceof String){
                alertPhoto = (String) obj;
            }
        }
        if (soapObject.hasProperty("isActive"))
        {
            Object obj = soapObject.getProperty("isActive");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                isActive = Boolean.parseBoolean(j.toString());
            }else if (obj!= null && obj instanceof Boolean){
                isActive = (Boolean) obj;
            }
        }
    }
    @Override
    public Object getProperty(int arg0) {
        switch(arg0){
            case 0:
                return alertTitle;
            case 1:
                return alertMessage;
            case 2:
                return alertInteger;
            case 3:
                return alertURL;
            case 4:
                return alertPhoto;
            case 5:
                return isActive;
        }
        return null;
    }
    
    @Override
    public int getPropertyCount() {
        return 6;
    }
    
    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "alertTitle";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "alertMessage";
                break;
            case 2:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "alertInteger";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "alertURL";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "alertPhoto";
                break;
            case 5:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "isActive";
                break;
        }
    }
    
    @Override
    public String getInnerText() {
        return null;
    }
    
    
    @Override
    public void setInnerText(String s) {
    }
    
    
    @Override
    public void setProperty(int arg0, Object arg1) {
    }
    
}