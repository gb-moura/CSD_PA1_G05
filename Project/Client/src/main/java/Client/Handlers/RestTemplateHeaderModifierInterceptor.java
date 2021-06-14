package Client.Handlers;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestTemplateHeaderModifierInterceptor implements ClientHttpRequestInterceptor {

    String token;

    public RestTemplateHeaderModifierInterceptor(String token){
        this.token = token;
    }


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add("token",token);
        return execution.execute(request, body);
    }
}
