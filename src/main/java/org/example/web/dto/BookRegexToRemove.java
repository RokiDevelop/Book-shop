package org.example.web.dto;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Valid
@EnableWebMvc
public class BookRegexToRemove {

    @NotEmpty
    @NotBlank
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
