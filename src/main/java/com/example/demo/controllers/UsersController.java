package com.example.demo.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;

import com.example.demo.models.document;


import com.example.demo.models.DocumentRepository;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class UsersController {  
    
    @Autowired
    private DocumentRepository repo;

    @GetMapping("view")
    public String getAllFiles(Model model) {
        System.out.println("pass");
        List<document> listDocs = repo.findAll();
        model.addAttribute("listDocs", listDocs);
        return "showAll";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("document") MultipartFile multipartFile, RedirectAttributes ra) throws IOException{
        //TODO: process POST request
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        document doc = new document();
        doc.setName(fileName);
        doc.setContent(multipartFile.getBytes());
        doc.setSize(multipartFile.getSize());
        doc.setUploadTime(new Date());

        repo.save(doc);

        ra.addFlashAttribute("message", "The file has been uploaded successfully.");

        return "success";
    }
    
    @GetMapping("download")
    public void downloadFile(@Param("id") long id, HttpServletResponse response) throws Exception{
        Optional<document> result = repo.findById(id);
        if(!result.isPresent()){
            throw new Exception("Document not found: id="+id);
        }
        document doc = result.get();
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + doc.getName();
        response.setHeader(headerKey, headerValue);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(doc.getContent());
        outputStream.close();
    }
    
    
    
}
