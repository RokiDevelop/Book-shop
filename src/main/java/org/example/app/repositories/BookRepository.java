package org.example.app.repositories;

import org.apache.log4j.Logger;
import org.example.app.exceptions.BookNotFoundException;
import org.example.app.exceptions.ItemNotFoundException;
import org.example.app.services.IdProvider;
import org.example.web.dto.Book;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class BookRepository implements IProjectRepository<Book>, ApplicationContextAware {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    private final List<Book> repo = new ArrayList<>();
    private ApplicationContext context;

    @Override
    public List<Book> retrieveAll() {
        return new ArrayList<>(repo);
    }

    @Override
    public boolean store(Book book) throws ItemNotFoundException {
        book.setId(context.getBean(IdProvider.class).provideId(book));

        boolean isSaved = repo.add(book);
        if (isSaved) {
            logger.info("Store new book:" + book);
            return true;
        } else {
            logger.info("Saved book (" + book + ") FAILED");
            throw new BookNotFoundException(book.toString());
        }
    }

    @Override
    public boolean removeItemById(String bookIdToRemove) throws ItemNotFoundException {
        for (Book book : retrieveAll()) {
            if (book.getId().equals(bookIdToRemove)) {
                logger.info("remove book completed: " + book);
                return repo.remove(book);
            }
        }

        logger.info("remove book with id(" + bookIdToRemove + ") FAILED");
        throw new BookNotFoundException("Book with id(" + bookIdToRemove + ") not found!");
    }

    @Override
    public int removeItemByRegex(String queryRegex) throws ItemNotFoundException{

        List<String> queryParts = getListQueryParts(queryRegex);

        List<String> queryWords = getListQueryWords(queryParts);

        String param = getParamIntoQuery(queryParts);

        int amountRemoved = 0;

        for (Book book : retrieveAll()) {
            if (param.trim().equals("author") && checkByAuthor(book, queryWords)) {
                amountRemoved++;
            }
            if (param.trim().equals("title") && checkByTitle(book, queryWords)) {
                amountRemoved++;
            }
            if (param.trim().equals("size") && checkBySize(book, queryWords)) {
                amountRemoved++;
            }
        }

        if (amountRemoved == 0) {
            throw new BookNotFoundException("Books by Regex(" + queryRegex + ") not found!");
        }

        return amountRemoved;
    }

    private boolean checkByAuthor(Book book, List<String> queryWords) {
        if (queryWords.contains(book.getAuthor())) {
            return removeFromRepo(book, "remove by Author(REGEX) book completed: " + book);
        }
        return false;
    }

    private boolean checkByTitle(Book book, List<String> queryWords) {
        if (queryWords.contains(book.getTitle())) {
            return removeFromRepo(book, "remove by Title(REGEX) book completed: " + book);
        }
        return false;
    }

    private boolean checkBySize(Book book, List<String> queryWords) {
        if (queryWords.contains(book.getSize().toString())) {
            return removeFromRepo(book, "remove by Size(REGEX) book completed: " + book);
        }
        return false;
    }


    private boolean removeFromRepo(Book book) {
        logger.info("remove book completed: " + book);
        return repo.remove(book);
    }

    private boolean removeFromRepo(Book book, String logMassage) {
        logger.info(logMassage);
        return repo.remove(book);
    }

    private List<String> getListQueryParts(String queryString) {
        List<String> stringList = Arrays.asList(
                queryString.replaceAll("[ ]{2,}", " ")
                        .split("[:=, ]"));
        stringList.forEach(String::trim);
        return stringList;
    }

    private List<String> getListQueryWords(List<String> queryParts) {
        List<String> queryList = new ArrayList<>();
        for (int i = 1; i < queryParts.size(); i++) {
            String s = queryParts.get(i);
            if (s.matches("([А-Яа-яA-Za-z0-9])+")) {
                queryList.add(s);
            }
        }
        return queryList;
    }

    private String getParamIntoQuery(List<String> list) {
        return list.get(0);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private void defaultInit() {
        logger.info("Provider default INIT in book repo bean " + this.hashCode());
    }

    private void defaultDestroy() {
        logger.info("Provider default DESTROY in book repo bean " + this.hashCode());
    }
}