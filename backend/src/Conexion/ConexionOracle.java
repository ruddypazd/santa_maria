package Conexion;

import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConexionOracle {
    public Connection con;
    public boolean loggedIn = false;
    private static ConexionOracle ConexionConectada = null;
    protected ResultSet rs;
    private String usr, pass, baseDatos, puerto, ip;

    public ConexionOracle(String usr, String pass, String baseDatos, String puerto, String ip) {
        loggedIn = false;
        this.usr = usr;
        this.pass = pass;
        this.baseDatos = baseDatos;
        this.puerto = puerto;
        this.ip = ip;
        ifConnected();
    }

    public boolean ifConnected() {
        try {
            if (loggedIn == true) {
                return true;
            } else {
                Conectar();
                return true;
            }
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
            return false;
        }
    }

    public void Conectar() throws SQLException {
        try {
            // Class.forName("oracle.jdbc.OracleDriver");
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            Login();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    private void Login() throws SQLException {
        String usr2 = "\"" + usr + "\"";
        String pass2 = pass;
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=" + ip
                    + ")(PORT=" + puerto + "))(CONNECT_DATA=(SERVER=SHARED)(SID=" + baseDatos + ")))", usr2, pass2);
        } catch (Exception e) {
            try {
                con = DriverManager.getConnection("jdbc:oracle:thin:@" + ip + ":" + puerto + ":" + baseDatos, usr2,
                        pass2);
            } catch (Exception t) {
                System.out.println(t.getMessage());
            }

        }

        loggedIn = true;
        setDatabaseConnection(this);
    }

    private static void setDatabaseConnection(ConexionOracle conexion) {
        ConexionOracle.ConexionConectada = conexion;
    }

    public static ConexionOracle getDatabaseConnection() {
        return ConexionOracle.ConexionConectada;
    }

    public void Close() {
        if (loggedIn) {
            try {
                con.close();
                loggedIn = false;
            } catch (SQLException ex) {
                loggedIn = false;
                System.out.print(ex.getMessage());
            }
        }
    }

    public static String getJSON(String consulta, ConexionOracle con) throws JSONException {
        try {
            PreparedStatement ps = con.statamet(consulta);
            ResultSet rs = ps.executeQuery();
            Clob clob = rs.next() ? rs.getClob("json") : null;
            rs.close();
            ps.close();

            if (clob != null) {
                Reader r = clob.getCharacterStream();
                StringBuilder buffer = new StringBuilder();
                int ch;
                while ((ch = r.read()) != -1) {
                    buffer.append((char) ch);
                }
                JSONObject obj = new JSONObject();
                obj.put("estado", "exito");
                obj.put("data", new JSONArray(buffer.toString()));
                return obj.toString();
            } else {
                JSONObject obj = new JSONObject();
                obj.put("estado", "error");
                obj.put("error", "Clob is null");
                return obj.toString();
            }
        } catch (Exception ex) {
            JSONObject obj = new JSONObject();
            obj.put("estado", "error");
            obj.put("error", ex.getLocalizedMessage());
            return obj.toString();
        }
    }

    public PreparedStatement statamet(String sql) {
        try {
            return con.prepareStatement(sql);
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
            return null;
        }
    }

    public String EjecutarDDL(String sentencia) throws SQLException, JSONException {
        String mensaje = "Se Ejecuto la Sentencia Correctamente";
        JSONObject obj = new JSONObject();
        try {
            Statement ps = con.createStatement();
            ps.execute(sentencia);
            ps.close();
            obj.put("estado", "exito");
            obj.put("error", mensaje);
            return obj.toString();
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getMessage());
            return  obj.toString();
        }

    }
    
    public void EjecutarSentencia(String sentencia) throws SQLException{        
        PreparedStatement ps = null;
        try{            
            ps = con.prepareStatement(sentencia);
            ps.execute();
            ps.close();
        }catch(Exception e){
            if(ps!=null) ps.close();
            throw e;
        }   
    }
   
}