package Client.Services;

import Client.Exceptions.NoMajorityAnswerException;
import Client.Exceptions.ServerAnswerException;
import Client.Util.SystemReply;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ExtractAnswer {

    public String extractAnswerGet (String url, RestTemplate restTemplate) throws ServerAnswerException {
        ResponseEntity<SystemReply> response =
                restTemplate.getForEntity(url, SystemReply.class);
        return extractFromResponse(response);
    }

    public <V> String extractAnswerPost (String url, V objPost,RestTemplate restTemplate) throws ServerAnswerException {
        ResponseEntity<SystemReply> response =
                restTemplate.postForEntity(url, objPost, SystemReply.class);
        return extractFromResponse(response);
    }



    private String extractFromResponse (ResponseEntity<SystemReply> response) throws ServerAnswerException {
        SystemReply systemReply = response.getBody();
        assert systemReply != null;
        try {
            if (!SignatureVerifier.isValidReply(systemReply))
                throw new NoMajorityAnswerException();
            return convertMostFrequentAnswer(systemReply.getReply()).toString();
        } catch (Exception e) {
            throw new ServerAnswerException(e.getMessage());
        }
    }

    private Object convertMostFrequentAnswer(byte[] answer) {

        return new Gson().fromJson(new String(answer), Object.class);
    }





}
