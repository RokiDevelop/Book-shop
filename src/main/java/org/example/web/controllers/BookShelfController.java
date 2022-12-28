package org.example.web.controllers;

import org.apache.log4j.Logger;
import org.example.app.exceptions.ItemNotFoundException;
import org.example.app.services.IEntityService;
import org.example.web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Files;

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
                reportAboutError("Invalid request!", redirectAttributes, "errorSave");

                return "redirect:/books/shelf";
            }

            bookService.save(book.convertToBook());
            logger.info("current repository size:" + bookService.getAll().size());

            return "redirect:/books/shelf";

        } catch (ItemNotFoundException bookNotFoundExeption) {
            reportAboutError("Save error!", redirectAttributes, "errorSave");

            return "redirect:/books/shelf";
        }
    }

    @PostMapping(value = "/removeById")
    public String removeBook(@Valid BookIdToRemove bookIdToRemove,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors() || !bookIdToRemove.isValid()) {
                reportAboutError("Must not be empty. Int and greater than 0!", redirectAttributes, "errorRemoveById");
                return "redirect:/books/shelf";
            }
            bookService.removeById(bookIdToRemove.id);
            return "redirect:/books/shelf";
        } catch (ItemNotFoundException bookNotFoundExeption) {
            reportAboutError(bookNotFoundExeption.getMessage(), redirectAttributes, "errorRemoveById");

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
                reportAboutError("Invalid request!", redirectAttributes, "errorRemoveByRegex");

                return "book_shelf";
            } else {
                String queryRegex = bookRegexToRemove.getRegex();
                int count = bookService.removeByRegex(queryRegex.trim());
                logger.info("Remove " + count + " book(s) by Regex(" + queryRegex + ")");

                return "redirect:/books/shelf";
            }
        } catch (ItemNotFoundException bookNotFoundExeption) {
            reportAboutError(bookNotFoundExeption.getMessage(), redirectAttributes, "errorRemoveByRegex");

            return "redirect:/books/shelf";
        }
    }

    @PostMapping(value = "/uploadFile")
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes)
            throws Exception {
        if (file.isEmpty()) {
            reportAboutError("Error upload! File is empty!", redirectAttributes, "errorUpload");

            return "redirect:/books/shelf";
        }

        String fileName = file.getOriginalFilename();
        byte[] bytes = file.getBytes();

        File dir;
        dir = getDirectory("external_upload");
        writeToFile(dir, fileName, bytes);

        return "redirect:/books/shelf";
    }

    public void reportAboutError(String errorMassage, RedirectAttributes redirectAttributes, String attributeName) {
        logger.info(errorMassage);
        redirectAttributes.addFlashAttribute(attributeName, errorMassage);
    }

    private File getDirectory(String dirName) {
        String rootPath = System.getProperty("catalina.home");
        File dir = new File(rootPath + File.separator + dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private void writeToFile(File dir, String fileName, byte[] bytes) throws Exception {
        File serverFile = new File(dir.getAbsolutePath() + File.separator + fileName);
        BufferedOutputStream stream = new BufferedOutputStream(Files.newOutputStream(serverFile.toPath()));
        stream.write(bytes);
        stream.close();

        logger.info("new file saved at:" + serverFile.getAbsoluteFile());
    }
}