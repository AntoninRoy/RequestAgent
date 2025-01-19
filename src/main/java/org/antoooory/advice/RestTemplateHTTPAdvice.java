package org.antoooory.advice;

import net.bytebuddy.asm.Advice;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

import java.net.URI;

public class ExecuteAdvice {

        @Advice.OnMethodEnter
        public static void onEnter(@Advice.Argument(0) String uriTemplate,
                                   @Advice.Argument(1) HttpMethod method) {
            System.out.println("RestTemplate execute() called with URI template: " + uriTemplate + ", method: " + method);
        }

        @Advice.OnMethodExit(onThrowable = RestClientException.class)
        public static <T>  void onExit(@Advice.Return(readOnly = false) Object returnValue,
                                  @Advice.Argument(0) URI url,
                                  @Advice.Argument(1) String uriTemplate,
                                  @Advice.Argument(2) HttpMethod method,
                                  @Advice.Argument(3) RequestCallback requestCallback,
                                  @Advice.Argument(4) ResponseExtractor<T> responseExtractor,
                                  @Advice.Thrown Throwable throwable) {
            System.out.println("RestTemplate execute() called with URI template: " + uriTemplate + ", method: " + method);
            System.out.println(returnValue.getClass().getName());
            if (throwable != null) {
                System.out.println("RestTemplate execute() threw an exception: " + throwable.getMessage());
            } else {
                System.out.println("RestTemplate execute() completed successfully.");
            }
        }
    }