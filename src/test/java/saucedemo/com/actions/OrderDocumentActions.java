package saucedemo.com.actions;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.stepdefinitions.Hooks;
import saucedemo.com.support.PdfContent;
import saucedemo.com.support.PdfDownloads;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderDocumentActions extends UIInteractionSteps {
    private static final Logger log = LoggerFactory.getLogger(OrderDocumentActions.class);

    private Path download;

    @Step("Download the order PDF")
    public void download_order_pdf() {
        Map<Path, String> state = PdfDownloads.snapshotState();
        PdfDownloads.prepareBrowserDownload(getDriver());
        $(By.id("generate-pdf-order")).waitUntilClickable().click();
        download = PdfDownloads.waitForNewOrUpdatedPdf(state, Duration.ofSeconds(45));
        if (download == null) {
            throw new IllegalStateException("No order PDF was downloaded in this scenario");
        }
        log.info("Downloaded order PDF: {}", download);
    }

    @Step("Verify the downloaded PDF contains the text: {0}")
    public void verify_downloaded_pdf_contains(String text) {
        if (download == null) throw new IllegalStateException("No order PDF has been downloaded in this scenario");
        try {
            assertThat(PdfContent.text(download).contains(text)).as("Expected text in downloaded order PDF").isTrue();
            log.info("Verified downloaded PDF contains the text: {}", text);
        } catch (java.io.IOException e) {
            log.error("Could not read downloaded order PDF", e);
            throw new IllegalStateException("Could not read downloaded order PDF", e);
        }
    }
}
