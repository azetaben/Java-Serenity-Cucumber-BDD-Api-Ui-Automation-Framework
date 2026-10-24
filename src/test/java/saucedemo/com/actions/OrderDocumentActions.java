package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import saucedemo.com.support.FrameworkLogger;
import saucedemo.com.support.PdfContent;
import saucedemo.com.support.PdfDownloads;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

public class OrderDocumentActions extends UIInteractionSteps {

    private Path download;

    @Step("Download the order PDF")
    public void download_order_pdf() {
        FrameworkLogger.actionStarted(OrderDocumentActions.class, "Download the order PDF");
        Map<Path, String> state = PdfDownloads.snapshotState();
        PdfDownloads.prepareBrowserDownload(getDriver());
        $(By.id("generate-pdf-order")).waitUntilClickable().click();
        download = PdfDownloads.waitForNewOrUpdatedPdf(state, Duration.ofSeconds(45));
    }

    @Step("Verify the downloaded PDF contains the text: {0}")
    public void verify_downloaded_pdf_contains(String text) {
        FrameworkLogger.actionStarted(OrderDocumentActions.class, "Verify the downloaded PDF contains the text: " + text);
        if (download == null) throw new IllegalStateException("No order PDF has been downloaded in this scenario");
        try {
            Assertions.assertTrue(PdfContent.text(download).contains(text), "Expected text in downloaded order PDF");
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not read downloaded order PDF", e);
        }
    }
}
