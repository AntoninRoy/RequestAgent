package org.antoooory.advice;

import net.bytebuddy.asm.Advice;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WebClientHTTPAdvice {
    //create Advice for this method :         private String getLogPrefix(ClientRequest request, ClientHttpResponse response) {
    //            String var10000 = request.logPrefix();
    //            return var10000 + "[" + response.getId() + "] ";
    //        }

    public static final List<String> logPrefixes = new ArrayList<>();

    public static AtomicInteger counter = new AtomicInteger(0);
    @Advice.OnMethodExit
    public static <T> void onExit(@Advice.This ClientResponse response, @Advice.Argument(0) BodyExtractor<T, ? super ClientHttpResponse> extractor, @Advice.Return(readOnly = false) T returnValue) {
        // Logique exécutée après l'exécution de la méthode
        System.out.println("[" + response.logPrefix() + "]" +  "Exiting getLogPrefix:");
        System.out.println("[" + response.logPrefix() + "]" +  "Request URI: " + response.request().getURI());
        System.out.println("[" + response.logPrefix() + "]" +  "Request Headers: " + response.request().getHeaders());

        System.out.println("[" + response.logPrefix() + "]" +  "Response status code: " + response.statusCode());
        System.out.println("[" + response.logPrefix() + "]" +  "Response status code: " + response.headers());

        if (returnValue instanceof Mono<?> mono) {
            logPrefixes.add(response.logPrefix());
            returnValue = (T)mono.doOnNext(WebClientHTTPAdvice::logElement);
        }

    }
    public static void logElement(Object element) {
        //ici envoyer la response via une classe anonyme (pas de lambda)
        synchronized (logPrefixes) {
            System.out.println(String.join(" ", logPrefixes) + " " + element);

            System.out.println("Counter add");
            if(logPrefixes.size() ==  counter.incrementAndGet()){
                counter.set(0);
                logPrefixes.clear();
                System.out.println("Reset");
            }

        }

    }
}
