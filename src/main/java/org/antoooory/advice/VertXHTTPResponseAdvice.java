package org.antoooory.advice;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.impl.HttpClientResponseImpl;
import net.bytebuddy.asm.Advice;

public class VertXHTTPResponseAdvice {
    @Advice.OnMethodEnter
    public static void onEnter(@Advice.This HttpClientResponseImpl response, @Advice.Argument(0) Buffer chunkBuffer) {
        System.out.println(response.request().hashCode() + " - " + response.request().absoluteURI());

        System.out.println(chunkBuffer.toString());
    }
}