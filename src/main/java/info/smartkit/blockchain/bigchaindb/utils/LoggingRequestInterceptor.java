package info.smartkit.blockchain.bigchaindb.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Created by yangboz on 10/1/15.
 *
 * @see: http://stackoverflow.com/questions/7952154/spring-resttemplate-how-to-enable-full-debugging-logging-of-requests-responses
 */
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    private final Logger LOG = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        ClientHttpResponse response = execution.execute(request, body);

        log(request, body, response);

        return response;
    }

    private void log(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        //do logging
        LOG.info("request:" + request.getURI().toString() + ",response:" + response.getStatusCode() + ",body:" + body.toString());
    }
}
