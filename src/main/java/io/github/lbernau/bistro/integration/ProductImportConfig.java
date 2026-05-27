package io.github.lbernau.bistro.integration;

import io.github.lbernau.bistro.properties.BistroApplicationProperties;
import io.github.lbernau.bistro.service.ProductImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessagingException;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(
                prefix = "io.github.lbernau.bistro.import",
                name = "enabled",
                havingValue = "true",
                matchIfMissing = true
)
public class ProductImportConfig {

    private final ProductImportService importService;

    private final BistroApplicationProperties properties;

    @Bean
    public IntegrationFlow watchImportDirectoryFlow() {
        return IntegrationFlow
                        .from(Files.inboundAdapter(new File(properties.importFolder()))
                                   .useWatchService(true)
                                   .watchEvents(FileReadingMessageSource.WatchEventType.CREATE)
                                   .autoCreateDirectory(true)
                                   .patternFilter("*.csv")
                                   .ignoreHidden(true)
                                   .preventDuplicates(true),
                                        //spec -> spec.poller( Pollers.cron("0 */15 * * * ?", TimeZone.getTimeZone("Europe/Berlin"))
                                        spec ->
                                                        spec.poller(Pollers.fixedDelay(Duration.of(30, ChronoUnit.SECONDS))
                                                                           .maxMessagesPerPoll(1)
                                                                           .errorChannel("productImportErrorChannel")))

                        .handle(File.class, (file, headers) -> {
                            importService.importProductCsv(file, headers);
                            return file;
                        })
                        .handle(getFileWritingMessageHandler(properties.successFolder()))
                        .nullChannel();
    }

    @Bean
    public IntegrationFlow errorFlow() {
        return IntegrationFlow
                        .from("productImportErrorChannel")
                        .<MessagingException, File>transform(
                                        ex -> {
                                            log.error(ex.getCause()
                                                        .getMessage(), ex);
                                            return (File) ex.getFailedMessage()
                                                            .getPayload();
                                        })
                        .handle(getFileWritingMessageHandler(properties.errorFolder()))
                        .nullChannel();
    }

    private FileWritingMessageHandler getFileWritingMessageHandler(final String target) {
        final FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(target));

        handler.setFileExistsMode(FileExistsMode.REPLACE);
        handler.setAutoCreateDirectory(true);
        handler.setDeleteSourceFiles(true);
        handler.setRequiresReply(false);
        handler.setFileNameGenerator(message ->
                        "%s_%s".formatted(
                                        System.currentTimeMillis(),
                                        ((File) message.getPayload()).getName()));

        return handler;
    }
}
