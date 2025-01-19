package org.antoooory;

import io.vertx.core.http.HttpClientRequest;
import net.bytebuddy.asm.Advice;
import io.netty.buffer.ByteBuf;
import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;

public class DoWriteAdvice {
    @Advice.OnMethodEnter
    public static void intercept(@Advice.This HttpClientRequest httpClientRequest,
                                 @Advice.Argument(0) ByteBuf buffer,
                                 @Advice.Argument(1) boolean end,
                                 @Advice.Argument(2) boolean connect,
                                 @Advice.Argument(3) Handler<AsyncResult<Void>> completionHandler) {
        if(httpClientRequest == null){
            System.out.println("Intercepted doWrite HttpClientRequest: null");
        }else{
            System.out.println(httpClientRequest.hashCode() + " - " + httpClientRequest.absoluteURI());
        }


        if(buffer != null){
            String body = buffer.toString(java.nio.charset.StandardCharsets.UTF_8);
            System.out.println("Intercepted doWrite Body: " + body);
            System.out.println("End flag: " + end + ", Connect flag: " + connect);
        }else{
            System.out.println("Intercepted doWrite Body: null");
        }


    }
}
