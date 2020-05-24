package com.valentine.demo.image;

import com.valentine.demo.DemoApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/images")
public class ImageController {

    @Autowired
    ImageService imageService;

    @GetMapping("/")
    public String imagePage(Model model){
        Image image = new Image();
        model.addAttribute("image", image);
        return "/image-test";
    }

    @PostMapping("/upload")
    public void uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        int nextNumber = imageService.getImageNumber() + 1;
        File destination = new File("src/main/resources/static/photos/img"+nextNumber+".png");
        if(!file.isEmpty()){
            BufferedImage src = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            ImageIO.write(src, "png", destination);
        }

        Image image = new Image();
        DemoApplication.logger.debug("Path: "+destination.getPath());
        DemoApplication.logger.debug("Absolute Path: "+destination.getAbsolutePath());
        DemoApplication.logger.debug("Canonical Path: "+destination.getCanonicalPath());
        image.setLocation(destination.getPath());
        imageService.save(image);
    }



}
