package jenkins.plugins.fogbugz;

import hudson.Extension;
import hudson.MarkupText;
import hudson.MarkupText.SubText;
import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogAnnotator;
import hudson.scm.ChangeLogSet.Entry;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Extension
public class FogbugzChangelogAnnotator extends ChangeLogAnnotator {
    private final Logger logger = Logger.getLogger(getClass().getName());

    private static final String VIEW_CASE_URL = "default.asp?";

    @Override
    public void annotate(AbstractBuild<?, ?> build, Entry change, MarkupText text) {
        FogbugzProjectProperty.DescriptorImpl descriptor = FogbugzProjectProperty.DESCRIPTOR;

        this.annotate(descriptor.getRegex(), descriptor.getUrl(), text);
    }

    public void annotate(String regex, String url, MarkupText text) {
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            logger.log(Level.WARNING, "Cannot compile pattern: {0}", regex);
            return;
        }

        for (SubText token : text.findTokens(pattern)) {
            try {
                Integer key = getId(token);
                String startTag = String.format("<a href=\"%s%s%d\">", url, VIEW_CASE_URL, key);

                token.surroundWith(startTag, "</a>");
            } catch (Exception e) {
                continue;
            }
        }
    }

    private int getId(SubText token) {
        for (int i = 0; ; i++) {
            String id = token.group(i);
            try {
                return Integer.valueOf(id);
            } catch (NumberFormatException e) {
                logger.log(Level.FINE, "{0} is not a number in group {1}, trying next group", new Object[]{id, i});
                continue;
            }
        }
    }

}
