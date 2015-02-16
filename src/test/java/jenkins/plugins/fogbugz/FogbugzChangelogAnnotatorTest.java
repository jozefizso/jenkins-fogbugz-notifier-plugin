package jenkins.plugins.fogbugz;

import hudson.MarkupText;
import org.junit.Test;

import static org.junit.Assert.*;

public class FogbugzChangelogAnnotatorTest {
    @Test
    public void annotate_ValidBugzIdExpression_ModifiesText() {
        String regex = "BugzID: (\\d+)";
        String url = "https://fogbugz.example.org/";
        MarkupText text = new MarkupText("Commit message with case number.\nBugzID: 1");

        String expectedText = "Commit message with case number.<br>" +
                "<a href=\"https://fogbugz.example.org/default.asp?1\">BugzID: 1</a>";

        FogbugzChangelogAnnotator annotator = new FogbugzChangelogAnnotator();

        // Act
        annotator.annotate(regex, url, text);

        // Assert
        String actualText = text.toString(false);

        assertEquals(expectedText, actualText);
    }
}
