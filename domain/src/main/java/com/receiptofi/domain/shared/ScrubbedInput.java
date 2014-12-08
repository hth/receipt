package com.receiptofi.domain.shared;

import com.receiptofi.utils.TextInputScrubber;

/**
 * User: hitender
 * Date: 12/8/14 6:02 AM
 */
public class ScrubbedInput {
    protected String text;

    public ScrubbedInput() {

    }

    public ScrubbedInput(String text) {
        this.text = TextInputScrubber.sanitize(TextInputScrubber.decode(text));
    }

    public String getText() {
        return text == null ? "" : text;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScrubbedInput that = (ScrubbedInput) o;
        return !(text != null ? !text.equals(that.text) : that.text != null);
    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }
}
