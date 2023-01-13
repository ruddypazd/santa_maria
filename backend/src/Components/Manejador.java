package Components;

import org.json.JSONObject;

public class Manejador {
    public Manejador(JSONObject obj) throws Exception {
        switch (obj.getString("component")) {
            case Apartamento.COMPONENT:
                Apartamento.onMessage(obj);
                break;
        }
    }

}
