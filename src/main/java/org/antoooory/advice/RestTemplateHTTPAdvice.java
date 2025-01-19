package org.antoooory.advice;

import net.bytebuddy.asm.Advice;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

import java.lang.reflect.Field;
import java.net.URI;

public class RestTemplateHTTPAdvice {

        @Advice.OnMethodExit(onThrowable = RestClientException.class)
        public static <T>  void onExit(@Advice.Return(readOnly = false) Object returnValue,
                                  @Advice.Argument(0) URI url,
                                  @Advice.Argument(1) String uriTemplate,
                                  @Advice.Argument(2) HttpMethod method,
                                  @Advice.Argument(3) RequestCallback requestCallback,
                                  @Advice.Argument(4) ResponseExtractor<T> responseExtractor,
                                  @Advice.Thrown Throwable throwable) {
            System.out.println("RestTemplate execute() called with URI template: " + uriTemplate + ", method: " + method);
            try {
                Field requestEntityField = requestCallback.getClass().getDeclaredField("requestEntity");
                requestEntityField.setAccessible(true);
                Object requestEntity = requestEntityField.get(requestCallback);
                if (requestEntity instanceof HttpEntity<?>) {
                    System.out.println("Headers: " + ((HttpEntity<?>) requestEntity).getHeaders());
                    System.out.println("Body: " + ((HttpEntity<?>) requestEntity).getBody());
                } else {
                    System.out.println("Headers: not found or null");
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                System.out.println("Failed to access requestEntity field: " + e.getMessage());
            }



            if (throwable != null) {
                System.out.println("RestTemplate execute() threw an exception: " + throwable.getMessage());
            } else {
                System.out.println("RestTemplate execute() completed successfully.");
            }

            if(returnValue instanceof ResponseEntity<?> responseEntity){
                System.out.println("ResponseEntity: " + responseEntity.getBody());
            }else{
                System.out.println("ResponseEntity: null");
            }



        }
    }