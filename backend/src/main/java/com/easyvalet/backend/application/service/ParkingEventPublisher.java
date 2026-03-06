package com.easyvalet.backend.application.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class ParkingEventPublisher {

    @Getter
    @AllArgsConstructor
    private static class EmitterEntry {
        private final Long userId;
        private final SseEmitter emitter;
    }

    private final Map<Long, List<EmitterEntry>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long affiliationId, Long userId) {
        SseEmitter emitter = new SseEmitter(300_000L); // 5 minutes
        EmitterEntry entry = new EmitterEntry(userId, emitter);
        emitters.computeIfAbsent(affiliationId, k -> new CopyOnWriteArrayList<>()).add(entry);

        emitter.onCompletion(() -> removeEmitter(affiliationId, entry));
        emitter.onTimeout(() -> removeEmitter(affiliationId, entry));
        emitter.onError(e -> removeEmitter(affiliationId, entry));

        return emitter;
    }

    public void publishUpdate(Long affiliationId, String eventType, Object data) {
        List<EmitterEntry> affiliationEmitters = emitters.get(affiliationId);
        if (affiliationEmitters == null) return;

        List<EmitterEntry> deadEntries = new java.util.ArrayList<>();
        for (EmitterEntry entry : affiliationEmitters) {
            try {
                entry.getEmitter().send(SseEmitter.event()
                        .name(eventType)
                        .data(data));
            } catch (IOException e) {
                deadEntries.add(entry);
            }
        }
        affiliationEmitters.removeAll(deadEntries);
    }

    public void publishUpdateExcluding(Long affiliationId, Long excludeUserId, String eventType, Object data) {
        List<EmitterEntry> affiliationEmitters = emitters.get(affiliationId);
        if (affiliationEmitters == null) return;

        List<EmitterEntry> deadEntries = new java.util.ArrayList<>();
        for (EmitterEntry entry : affiliationEmitters) {
            if (entry.getUserId().equals(excludeUserId)) continue;
            try {
                entry.getEmitter().send(SseEmitter.event()
                        .name(eventType)
                        .data(data));
            } catch (IOException e) {
                deadEntries.add(entry);
            }
        }
        affiliationEmitters.removeAll(deadEntries);
    }

    private void removeEmitter(Long affiliationId, EmitterEntry entry) {
        List<EmitterEntry> list = emitters.get(affiliationId);
        if (list != null) {
            list.remove(entry);
        }
    }
}
