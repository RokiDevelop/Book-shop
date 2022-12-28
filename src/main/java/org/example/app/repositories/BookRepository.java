package org.example.app.repositories;

import org.apache.log4j.Logger;
import org.example.app.exceptions.BookNotFoundException;
import org.example.app.exceptions.ItemNotFoundException;
import org.example.app.services.IdProvider;
import org.example.web.dto.Book;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
    public boolean store(Book book) {
        book.setId(context.getBean(IdProvider.class).provideId(book));

        repo.add(book);
        logger.info("Store new book:" + book);
        return true;
    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) throws ItemNotFoundException {
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
    public int removeItemByRegex(String queryRegex) throws ItemNotFoundException {

        if (!queryRegex.matches("([A-Za-zА-Яа-я ])+(=).([A-Za-zА-Яа-я0-9, .!?])+")) {
            throw new BookNotFoundException("Error template Regex");
        }

        List<String> queryParts = getListQueryParts(queryRegex);

        List<String> queryWords = getListQueryWords(queryParts);

        String param = getParamIntoQuery(queryParts);

        parameterValidityCheckByBook(param);

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
            if (param.trim().equals("id") && checkById(book, queryWords)) {
                amountRemoved++;
            }
        }

        if (amountRemoved == 0) {
            throw new BookNotFoundException("Books by Regex(" + queryRegex + ") not found!");
        }

        return amountRemoved;
    }

    private boolean checkByAuthor(Book book, List<String> queryWords) {
        if (queryWords.contains(book.getAuthor().toLowerCase(Locale.ROOT))) {
            return removeFromRepo(book, "remove by Author(REGEX) book completed: " + book);
        }
        return false;
    }

    private void parameterValidityCheckByBook(String param) throws BookNotFoundException {
        Field[] fields = Book.class.getDeclaredFields();

        for (Field field : fields) {
            if (param.equals(field.getName())) {
                return;
            }
        }

        logger.info("Invalid parameter Regex");
        throw new BookNotFoundException("Invalid parameter Regex");
    }

    private boolean checkById(Book book, List<String> queryWords) {
        if (queryWords.contains(book.getId().toString())) {
            return removeFromRepo(book, "remove by Id(REGEX) book completed: " + book);
        }
        return false;
    }

    private boolean checkByTitle(Book book, List<String> queryWords) {
        if (queryWords.contains(book.getTitle().toLowerCase(Locale.ROOT))) {
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

    private boolean removeFromRepo(Book book, String logMassage) {
        logger.info(logMassage);
        return repo.remove(book);
    }

    private List<String> getListQueryParts(String queryString) {
        List<String> stringList = Arrays.asList(
                queryString.replaceAll("[ ]{2,}", " ")
                        .split("[=,]"));
        stringList = stringList.stream().map(String::trim).map(String::toLowerCase).collect(Collectors.toList());
        return stringList;
    }

    private List<String> getListQueryWords(List<String> queryParts) {
        List<String> queryList = new ArrayList<>();
        for (int i = 1; i < queryParts.size(); i++) {
            String s = queryParts.get(i).trim();
            if (s.matches("([А-Яа-яA-Za-z0-9_.!? ])+")) {
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
