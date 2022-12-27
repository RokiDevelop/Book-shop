package org.example.web.dto;

import javax.validation.constraints.*;

public class BookToSave {

    @NotEmpty
    @Size(min = 1)
    private String author;

    @NotEmpty
    @Size(min = 1)
    private String title;

    @NotNull
    @NotEmpty
    @Min(1)
    @Size(min = 1)
    private Integer size;

    public BookToSave() {
    }

    public boolean isValid() {
        boolean isValidAuthor = author != null && !author.trim().isEmpty();
        boolean isValidTitle = title != null && !title.trim().isEmpty();
        boolean isValidSize = size != null && size > 0;

        return isValidAuthor && isValidTitle && isValidSize;
    }

    public Book convertToBook() {
        Book resultBook = new Book();
        resultBook.setSize(size);
        resultBook.setAuthor(author);
        resultBook.setTitle(title);
        return resultBook;
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
