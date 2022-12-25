package org.example.web.dto;


import javax.validation.constraints.*;

public class BookToSave {

    @NotNull
    private String author = null;

    @NotBlank
    @NotEmpty
    private String title = null;

    @Digits(integer = 4, fraction = 0)
    private Integer size;

    public BookToSave() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
