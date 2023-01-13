package Components;

import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import Conexion.ConexionPostgres;

public class Apartamento {
    public static final String COMPONENT = "apartamento";

    public static void onMessage(JSONObject obj) throws Exception {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj);
                break;
            case "registro":
                registro(obj);
                break;
            case "editar":
                editar(obj);
                break;
            case "delete":
                delete(obj);
                break;
            case "getByKey":
                getByKey(obj);
                break;
        }
    }

    public static void getByKey(JSONObject obj) throws JSONException {
        try {
            String consulta = "select get_by_key('" + COMPONENT +"','"+obj.getString("campo")+"','"+obj.getString("key")+"') as json";
            JSONObject data = ConexionPostgres.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getAll(JSONObject obj) throws JSONException {
        try {
            String consulta = "select get_all('" + COMPONENT + "') as json";
            JSONObject data = ConexionPostgres.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void editar(JSONObject obj) throws JSONException {
        try {
            JSONObject data = obj.getJSONObject("data");
            ConexionPostgres.editObject("areas", data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void delete(JSONObject obj) throws JSONException {
        try {
            JSONObject data = obj.getJSONObject("data");
            String key = data.getString("key");
            ConexionPostgres.anular(COMPONENT, key);
            obj.put("data", "Se Elimino Correctamente");
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void registro(JSONObject obj) throws JSONException {
        try {
            JSONObject data = obj.getJSONObject("data");
            data.put("key", UUID.randomUUID().toString());
            data.put("estado", 1);
            data.put("fecha_on", "now()");
            ConexionPostgres.insertArray(COMPONENT, new JSONArray().put(data));
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
