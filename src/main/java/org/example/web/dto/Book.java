package org.example.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Book {

    private String id;

    @NotNull
    private String author;

    @Size(min = 1, max = 10)
    private String title;

    @NotNull
    private Integer size;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Book() {
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

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", size=" + size +
                '}';
    }
}