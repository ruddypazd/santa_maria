package Http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import Components.Manejador;
import Conexion.ConexionPostgres;
import Utils.Utils;

public class httpServer {

    public httpServer() {
        try{
            System.out.println(">>> Iniciando server http..");
            JSONObject config = Utils.getConfig();
            JSONObject local = config.getJSONObject("httpServer");

            //HttpServer server = HttpServer.create(new InetSocketAddress(config.getInt("port")), 0);
            HttpServer server = HttpServer.create(new InetSocketAddress(local.getInt("port")), 0);
            ConexionPostgres.setConexion(config.getJSONObject("bdPostgres"));


            server.createContext("/", new Handler());
            server.setExecutor(null); // creates a default executor
            server.start();

        }catch(Exception e){
            e.printStackTrace();
        }   
    }

    static class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "";
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            switch(t.getRequestMethod()){
                case "POST":
                    post(t);
                break;
                default:
                    response = "Strasol Srl. no acepta peticiones "+t.getRequestMethod();
            }

          //  send(t, response);
        }
    }

    public static void post(HttpExchange t){
        
        try{
            InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8"); 
            BufferedReader br = new BufferedReader(isr);

            int b;
            StringBuilder buf = new StringBuilder(512);
            while ((b = br.read()) != -1) {
                buf.append((char) b);
            }

            br.close();
            isr.close();

            JSONObject obj = new JSONObject();
            if(buf.length()>0){
                obj = new JSONObject(buf.toString());
            }
            new Manejador(obj);

            send(t, obj.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void send(HttpExchange t, String response){
        try{
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}