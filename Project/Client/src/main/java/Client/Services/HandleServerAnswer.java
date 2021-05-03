package Client.Services;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import Client.Exceptions.ServerAnswerException;

public class HandleServerAnswer<E> {

    E processServerAnswer (ResponseEntity<String> response, Class<E> klass) throws ServerAnswerException {
        if (!response.getStatusCode().is2xxSuccessful()) {
            JSONObject obj = new JSONObject(response.getBody());
            String message = (String) obj.get("message");
            throw new ServerAnswerException(message);
        }
        System.out.println("HELLO IM HERE " + response.getBody());
        return new Gson().fromJson(response.getBody(), klass);
    }
}