package fitpay.engtest.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class FitpayCompositeController {

    @Resource
    private OAuth2RestTemplate oauth2RestTemplate;

    @Value("${fitpayUrl}")
    private String baseUrl;

    @GetMapping(value = "/compositeUsers/{userId}", produces = "application/json")
    public ResponseEntity getComposite(@PathVariable String userId,
                                       @RequestParam(required = false) String creditCardState,
                                       @RequestParam(required = false) String deviceState) {
        ResponseEntity<Map<String, Object>> responseStr = null;
        var response = new HashMap<>();
        try {
            responseStr = oauth2RestTemplate.exchange(baseUrl + userId, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
            });
            response.put("userInfo", responseStr.getBody());
            responseStr = oauth2RestTemplate.exchange(baseUrl + userId + "/devices", HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
            });
            response.put("deviceInfo", getFiltered(responseStr.getBody(), deviceState));

            responseStr = oauth2RestTemplate.exchange(baseUrl + userId + "/creditCards", HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
            });
            response.put("creditCardInfo", getFiltered(responseStr.getBody(), creditCardState));

            return new ResponseEntity(response, HttpStatus.OK);
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().is4xxClientError())
                return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Object getFiltered(Map res, String filter) {
        if(filter == null)
            return res.get("results");
        int totalResults = (int) res.get("totalResults");
        if (totalResults > 0) {
            List<Map> results = (List) res.get("results");
            return results.parallelStream().filter(x -> x.get("state").equals(filter))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}