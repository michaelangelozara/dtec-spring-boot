package com.DTEC.Document_Tracking_and_E_Clearance.async;

import com.DTEC.Document_Tracking_and_E_Clearance.message.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class AsyncMessageApi {

    private static final Logger log = LoggerFactory.getLogger(AsyncMessageApi.class);
    private final MessageService messageService;
    private final Executor executor;

    public AsyncMessageApi(MessageService messageService, Executor executor) {
        this.messageService = messageService;
        this.executor = executor;
    }

    public void notifyAllUsers(List<String> numbers, String message) {
        log.info("Notifying All Users");

        List<List<String>> partitionedList = partition(numbers, 500);

        List<CompletableFuture<Void>> futures = partitionedList
                .stream()
                .map(batch -> CompletableFuture.runAsync(() -> sendMessageUserBatch(batch, message), executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("All Users Notified");
    }

    private void sendMessageUserBatch(List<String> numbers, String message) {
        numbers.forEach(number -> {
            this.messageService.sendMessage(number, message);
        });
    }

    private List<List<String>> partition(List<String> list, int partitionSize) {
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += partitionSize) {
            partitions.add(list.subList(i, Math.min(i + partitionSize, list.size())));
        }
        return partitions;
    }


}
