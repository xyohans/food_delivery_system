/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author yohan
 */
public class Address {
    
    private int id;
    private int user_id;
    private String label;
    private String address;
    private boolean is_default;
    
    
    public int getID()              {return id;}
    public int getUserID()          {return user_id;}
    public String getLabel()        {return label;}
    public String getAddress()      {return address;}
    public boolean getIsDefault()   {return is_default;}
    
    public void setID(int id)                    {this.id =id;}
    public void setUserID(int user_id)           {this.user_id = user_id;}
    public void setLabel(String label)           {this.label = label;}
    public void setAddress(String address)       {this.address = address;}
    public void setIsDefault(boolean is_default) {this.is_default = is_default;}
    
    
    
}
