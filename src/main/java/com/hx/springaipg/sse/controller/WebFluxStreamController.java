package com.hx.springaipg.sse.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/stream")
public class WebFluxStreamController {

    private final ChatClient chatClient;

    public WebFluxStreamController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    // WebFlux 里直接返回 Flux，框架自动以 SSE 方式响应
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content()
                // 可以在 Flux 上做各种操作
                .doOnNext(chunk -> System.out.print(chunk))      // 每片到来时打印
                .doOnComplete(() -> System.out.println("\n完成")); // 全部完成时
    }
}
