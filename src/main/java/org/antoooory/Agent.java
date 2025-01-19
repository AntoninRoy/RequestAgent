package org.antoooory;

import io.vertx.core.buffer.Buffer;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.antoooory.advice.RestTemplateHTTPAdvice;
import org.antoooory.advice.VertXHTTPRequestAdvice;
import org.antoooory.advice.VertXHTTPResponseAdvice;
import org.antoooory.advice.WebClientHTTPAdvice;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;

import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.security.ProtectionDomain;

public class Agent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Byte Buddy Agent started for intercepting Vert.x doWrite method.");

        new AgentBuilder.Default()
                .type(ElementMatchers.named("io.vertx.core.http.impl.HttpClientRequestImpl"))
                .transform((DynamicType.Builder<?> builder,
                            TypeDescription typeDescription,
                            ClassLoader classLoader,
                            JavaModule module,
                            ProtectionDomain protectionDomain) ->
                        builder.method(ElementMatchers.named("doWrite")
                                        .and(ElementMatchers.takesArguments(4))
                                        .and(ElementMatchers.takesArgument(0, ElementMatchers.named("io.netty.buffer.ByteBuf")))
                                        .and(ElementMatchers.takesArgument(1, boolean.class))
                                        .and(ElementMatchers.takesArgument(2, boolean.class))
                                        .and(ElementMatchers.takesArgument(3, ElementMatchers.named("io.vertx.core.Handler"))))
                                .intercept(Advice.to(VertXHTTPRequestAdvice.class))
                ).installOn(inst);

        new AgentBuilder.Default()
                .type(ElementMatchers.named("io.vertx.core.http.impl.HttpClientResponseImpl"))
                .transform((DynamicType.Builder<?> builder,
                            TypeDescription typeDescription,
                            ClassLoader classLoader,
                            JavaModule module,
                            ProtectionDomain protectionDomain) ->
                        builder.method(ElementMatchers.named("handleChunk")
                                        .and(ElementMatchers.takesArguments(1))
                                        .and(ElementMatchers.takesArgument(0, Buffer.class)))
                                .intercept(Advice.to(VertXHTTPResponseAdvice.class))
                ).installOn(inst);


        new AgentBuilder.Default()
                .type(ElementMatchers.nameContains("DefaultClientResponse"))
                .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                                builder
                                        .method(
                                                ElementMatchers.named("body")
                                        )
                                        .intercept(Advice.to(WebClientHTTPAdvice.class))
                        //marche aussi
//                        builder.visit(Advice.to(HandleBodyToMonoAdvice.class).on(ElementMatchers.named("handleBodyMono")))
                )
                .installOn(inst);
        new AgentBuilder.Default()
                .type(ElementMatchers.named("org.springframework.web.client.RestTemplate"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> builder
                        .method(ElementMatchers.named("doExecute")
                                .and(ElementMatchers.takesArguments(5))
                                .and(ElementMatchers.takesArgument(0, URI.class))
                                .and(ElementMatchers.takesArgument(1, String.class))
                                .and(ElementMatchers.takesArgument(2, HttpMethod.class))
                                .and(ElementMatchers.takesArgument(3, RequestCallback.class))
                                .and(ElementMatchers.takesArgument(4, ResponseExtractor.class))
                                )
                        .intercept(Advice.to(RestTemplateHTTPAdvice.class)))
                .installOn(inst);
    }
}
