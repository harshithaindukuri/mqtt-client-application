package system.smartbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MenuActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storeid);
        String username = getIntent().getExtras().getString("user");
        Toast.makeText(this, "name ="+username, Toast.LENGTH_SHORT).show();
        String password = getIntent().getExtras().getString("pass");

        try{

            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con= DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe","system","harshi");
//here xe is database name
            Toast.makeText(this, "try next", Toast.LENGTH_SHORT).show();
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from users");
            Toast.makeText(this,"username"+rs.getString(2), Toast.LENGTH_SHORT).show();
            con.close();
        }catch(Exception e){
            Toast.makeText(this, " "+e.toString(), Toast.LENGTH_SHORT).show();}

    }
}

