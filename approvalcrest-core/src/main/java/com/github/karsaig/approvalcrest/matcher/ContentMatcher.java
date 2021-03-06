package com.github.karsaig.approvalcrest.matcher;

import java.util.function.Function;
import java.util.regex.Pattern;

import org.hamcrest.Description;

import com.github.karsaig.approvalcrest.FileMatcherConfig;
import com.github.karsaig.approvalcrest.matcher.file.AbstractDiagnosingFileMatcher;
import com.github.karsaig.approvalcrest.matcher.file.FileStoreMatcherUtils;

/**
 * <p>
 * Matcher for asserting expected {@link String}s. Searches for an approved file
 * in the same directory as the test file:
 * <ul>
 * <li>If found, the matcher will assert the contents of the file to the actual
 * {@link String}.</li>
 * <li>If not found, a non-approved file is created, that must be verified and
 * renamed to "*-approved.content" by the developer.</li>
 * </ul>
 * The files and directories are hashed with SHA-1 algorithm by default to avoid
 * too long file and path names. These are generated in the following way:
 * <ul>
 * <li>the directory name is the first {@value #NUM_OF_HASH_CHARS} characters of
 * the hashed <b>class name</b>.</li>
 * <li>the file name is the first {@value #NUM_OF_HASH_CHARS} characters of the
 * hashed <b>test method name</b>.</li>
 * </ul>
 * <p>
 * This default behavior can be overridden by using the
 * {@link #withFileName(String)} for custom file name and
 * {@link #withPathName(String)} for custom path.
 * </p>
 *
 * @param <T> Only {@link String} is supported at the moment.
 */
public class ContentMatcher<T> extends AbstractDiagnosingFileMatcher<T, ContentMatcher<T>> {

    private static final Pattern WINDOWS_NEWLINE_PATTERN = Pattern.compile("\r\n");

    private String expectedContent;

    public ContentMatcher(TestMetaInformation testMetaInformation, FileMatcherConfig fileMatcherConfig) {
        super(testMetaInformation, fileMatcherConfig, new FileStoreMatcherUtils("content", fileMatcherConfig));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(expectedContent);
    }

    @Override
    protected boolean matches(Object actual, Description mismatchDescription) {
        boolean matches = false;
        init();
        if (createNotApprovedFileIfNotExists(actual)
                && fileMatcherConfig.isPassOnCreateEnabled()) {
            return true;
        }
        initExpectedFromFile();
        String actualString = String.class.cast(actual);

        String actualNormalized = normalize(actualString);
        if (expectedContent.equals(actualNormalized)) {
            matches = true;
        } else {
            if (fileMatcherConfig.isOverwriteInPlaceEnabled()) {
                overwriteApprovedFile(actualNormalized);
                matches = true;
            } else {
                matches = appendMismatchDescription(mismatchDescription, expectedContent, actualNormalized,
                        getAssertMessage(fileStoreMatcherUtils, "Content does not match!"));
            }
        }
        return matches;
    }

    private String normalize(String input) {
        return WINDOWS_NEWLINE_PATTERN.matcher(input).replaceAll("\n");
    }

    private boolean createNotApprovedFileIfNotExists(Object toApprove) {
        return createNotApprovedFileIfNotExists(toApprove, () -> {
            if (!String.class.isInstance(toApprove)) {
                throw new IllegalArgumentException("Only String content matcher is supported!");
            }
            return normalize(String.class.cast(toApprove));
        });
    }

    private void overwriteApprovedFile(Object actual) {
        overwriteApprovedFile(actual, () -> String.class.cast(actual));
    }

    private void initExpectedFromFile() {
        expectedContent = normalize(getExpectedFromFile(Function.identity()));
    }
}
