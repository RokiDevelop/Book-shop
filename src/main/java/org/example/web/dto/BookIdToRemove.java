package org.example.web.dto;

import javax.validation.constraints.*;

public class BookIdToRemove {

    @NotNull
    @NotEmpty
    @Min(1)
    @Size(min = 1)
    public Integer id;

    public BookIdToRemove() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isValid() {
        return id != null && id > 0 && !id.toString().trim().isEmpty();
    }
}
