package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.app.exceptions.BookNotFoundException;
import org.example.app.exceptions.ItemNotFoundException;
import org.example.app.repositories.IProjectRepository;
import org.example.web.dto.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService implements IEntityService<Book> {

    private Logger logger = Logger.getLogger(LoginService.class);
    private final IProjectRepository<Book> bookRepo;

    @Autowired
    public BookService(IProjectRepository<Book> bookRepo) {
        this.bookRepo = bookRepo;
    }

    @Override
    public List<Book> getAll() {
        return bookRepo.retrieveAll();
    }

    @Override
    public boolean save(Book book) throws ItemNotFoundException {
        book.setAuthor(book.getAuthor().trim());
        book.setTitle(book.getTitle().trim());

        boolean validAuthor = !book.getAuthor().isEmpty();
        boolean validTitle = !book.getTitle().isEmpty();
        boolean validSize = book.getSize() != null;

        if (validAuthor || validTitle || validSize) {
            return bookRepo.store(book);
        }

        logger.info("Save book FAILED: " + book + ". INVALID params");
        throw new BookNotFoundException("Book is empty!");
    }

    @Override
    public boolean removeById(String bookIdToRemove) throws ItemNotFoundException {
        return bookRepo.removeItemById(bookIdToRemove);
    }

    @Override
    public int removeByRegex(String queryRegex) throws ItemNotFoundException{
        return bookRepo.removeItemByRegex(queryRegex);
    }

    private void defaultInit() {
        logger.info("Provider default INIT in book service bean " + this.hashCode());
    }

    private void defaultDestroy() {
        logger.info("Provider default DESTROY in book service bean " + this.hashCode());
    }
}
