package Utils;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

    private static JSONObject config = null;
    private static Session session = null;
    private static ChannelSftp sftpChannel;

    public static JSONObject getConfig(){
        try {

            if (config != null){
                return config;
            }
            System.out.println(">>>Leyendo archivos config.json");
            FileReader file = new FileReader("config.json");
            int valor = file.read();
            String configJson = "";
            while (valor != -1) {
                configJson = configJson + String.valueOf(((char) valor));
                valor = file.read();
                System.out.print(".");
            }
            file.close();
            config = new JSONObject(configJson);
            System.out.println(">>>100% Ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }
    public static boolean setFile(InputStream in){
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            String ruta = config.getString("ruta");
            sftpChannel = (ChannelSftp) channel;
            String rutas[] = ruta.split("/");
            String path="";
            for (int i = 0; i < rutas.length-1; i++) {
                path+=rutas[i]+"/";
                try{
                    sftpChannel.stat(path);
                }catch(Exception e){
                    sftpChannel.mkdir(path);
                }
            }
            
            sftpChannel.put(in,ruta);
            return true;
        } catch (Exception e) {
            e.printStackTrace();  
            return false;
        }
    }
    public static void Desconectar(){
        if(sftpChannel!=null)sftpChannel.exit();
        if(session!=null)session.disconnect();
    }
    public static void saveFile(JSONObject json) throws JSONException{
        
        InputStream in;
        int usuario_id = 1;
        String sfoto = "";
        String fecha_hora = "";
        if (json != null) {
            sfoto = json.getString("name");
            if (sfoto.length() > 0) {
                in = new ByteArrayInputStream(json.toString().getBytes());
                setFile(in);
            }
        }    
    }
}
