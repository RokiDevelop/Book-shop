package org.example.web.dto;

import javax.validation.constraints.NotBlank;

public class BookIdToRemove {

    @NotBlank
    public String id;

    public BookIdToRemove() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
