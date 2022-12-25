package org.example.web.controllers;

import org.apache.log4j.Logger;
import org.example.app.exceptions.BookNotFoundException;
import org.example.app.exceptions.ItemNotFoundException;
import org.example.app.services.IEntityService;
import org.example.web.dto.Book;
import org.example.web.dto.BookIdToRemove;
import org.example.web.dto.BookRegexToRemove;
import org.example.web.dto.BookToSave;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/books")
@Scope("session")
public class BookShelfController {

    private Logger logger = Logger.getLogger(BookShelfController.class);
    private IEntityService bookService;
    private BookRegexToRemove bookRegexToRemove;

    @Autowired
    public BookShelfController(IEntityService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(value = "/shelf")
    public String books(Model model) {
        logger.info("got book shelf");
        model.addAttribute("book", new Book());
        model.addAttribute("bookIdToRemove", new BookIdToRemove());
        model.addAttribute("bookToSave", new BookToSave());
        model.addAttribute("bookRegexToRemove", new BookRegexToRemove());
        model.addAttribute("bookList", bookService.getAll());
        return "book_shelf";
    }

    @PostMapping(value = "/save")
    public String saveBook(@Valid Book book,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("saveErr");
                return "redirect:/books/shelf";
            }
            bookService.save(book);
            logger.info("current repository size:" + bookService.getAll().size());
            return "redirect:/books/shelf";

        } catch (ItemNotFoundException bookNotFoundExeption) {
            String errorMassage = bookNotFoundExeption.getMessage();
            logger.info(errorMassage);

            return "redirect:/books/shelf";
        }
    }

    @PostMapping(value = "/removeById")
    public String removeBook(
            @Valid BookIdToRemove bookIdToRemove,
            BindingResult bindingResult,
            Model model) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("book", new Book());
                model.addAttribute("bookList", bookService.getAll());
                return "book_shelf";
            } else {
                bookService.removeById(bookIdToRemove.id);
                return "redirect:/books/shelf";
            }
        } catch (ItemNotFoundException bookNotFoundExeption) {
            String errorMassage = bookNotFoundExeption.getMessage();
            logger.info(errorMassage);
            return "redirect:/books/shelf";
        }
    }

    @PostMapping(value = "/removeByRegex")
    public String removeByRegex(
            @Valid BookRegexToRemove bookRegexToRemove,
            BindingResult bindingResult,
            Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "book_shelf";
            } else {
                String queryRegex = bookRegexToRemove.getRegex();
                bookService.removeByRegex(queryRegex.trim());
                return "redirect:/books/shelf";
            }
        } catch (ItemNotFoundException bookNotFoundExeption) {
            String errorMassage = bookNotFoundExeption.getMessage();

            logger.info(errorMassage);

            return "redirect:/books/shelf";
        }
    }
}