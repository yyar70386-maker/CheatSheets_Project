package com.library.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
  
  public static void main(String args[]) {
    Connection con= getConnection();
    
    if(con!=null) {
      System.out.println("connection is working");
    }
    
    
  }
  
  public static Connection getConnection() {
    Connection con= null;
  
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
     // System.out.println(con);
    } catch (ClassNotFoundException e) {
      System.out.println("Driver Error :"+e.getMessage());
      
    }
    try {
      con =DriverManager.getConnection("jdbc:mysql://localhost:3306/cheat_sheets","root","root");
      
      //System.out.println("Connection is successfully");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      System.out.println("Connection Error :"+e.getMessage());
    }
    return con;
  }

}