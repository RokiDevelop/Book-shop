package org.example.web.controllers;

import org.apache.log4j.Logger;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@Validated
@RequestMapping(value = "/books")
@Scope("session")
public class BookShelfController {

    private final Logger logger = Logger.getLogger(BookShelfController.class);
    private final IEntityService<Book> bookService;

    @Autowired
    public BookShelfController(IEntityService<Book> bookService) {
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
    public String saveBook(@Valid BookToSave book,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors() || !book.isValid()) {
                redirectAttributes.addFlashAttribute("errorSave",
                        "Save error!");
                return "redirect:/books/shelf";
            }

            bookService.save(book.convertToBook());
            logger.info("current repository size:" + bookService.getAll().size());
            return "redirect:/books/shelf";

        } catch (ItemNotFoundException bookNotFoundExeption) {
            logger.info(bookNotFoundExeption.getMessage());
            redirectAttributes.addFlashAttribute("errorSave",
                    "Save error!");
            return "redirect:/books/shelf";
        }
    }

    @PostMapping(value = "/removeById")
    public String removeBook(@Valid BookIdToRemove bookIdToRemove,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors() || !bookIdToRemove.isValid()) {
                redirectAttributes.addFlashAttribute("errorRemoveById", "");
                return "redirect:/books/shelf";
            }
            bookService.removeById(bookIdToRemove.id);
            return "redirect:/books/shelf";
        } catch (ItemNotFoundException bookNotFoundExeption) {
            logger.info(bookNotFoundExeption.getMessage());
            redirectAttributes.addFlashAttribute("errorRemoveById", "");
            return "redirect:/books/shelf";
        }
    }

    @PostMapping(value = "/removeByRegex")
    public String removeByRegex(
            @Valid BookRegexToRemove bookRegexToRemove,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("errorRemoveByRegex", "");
                return "book_shelf";
            } else {
                String queryRegex = bookRegexToRemove.getRegex();
                bookService.removeByRegex(queryRegex.trim());
                return "redirect:/books/shelf";
            }
        } catch (ItemNotFoundException bookNotFoundExeption) {
            String massageExeption = bookNotFoundExeption.getMessage();
            logger.info(massageExeption);
            redirectAttributes.addFlashAttribute("errorRemoveByRegex", massageExeption);
            return "redirect:/books/shelf";
        }
    }
}