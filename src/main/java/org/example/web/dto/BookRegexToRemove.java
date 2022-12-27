package org.example.web.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class BookRegexToRemove {

    @NotEmpty
    @NotBlank
    @NotNull
    private String regex;

    public BookRegexToRemove() {
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
