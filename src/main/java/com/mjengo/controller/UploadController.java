package com.mjengo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles file uploads from Survey, Planning, and Monitoring modules.
 * Saves files to an uploads/ directory inside the project root.
 */
@Controller
public class UploadController {

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file,
            @RequestParam("module") String module,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("uploadError", "Please select a file to upload.");
            return "redirect:/" + module;
        }

        try {
            // Create module-specific subdirectory
            Path uploadPath = Paths.get(UPLOAD_DIR, module);
            Files.createDirectories(uploadPath);

            // Save file
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            redirectAttributes.addFlashAttribute("uploadSuccess",
                    "File '" + file.getOriginalFilename() + "' uploaded successfully (" +
                            (file.getSize() / 1024) + " KB).");

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("uploadError",
                    "Upload failed: " + e.getMessage());
        }

        return "redirect:/" + module;
    }
}
