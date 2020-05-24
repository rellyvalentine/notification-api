package com.valentine.demo.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {

    @Autowired
    ImageRepository imageRepo;

    public void save(Image s){
        imageRepo.save(s);
    }

    public List<Image> getAllImages(){
        return imageRepo.findAll();
    }

    public int getImageNumber(){
        return getAllImages().size();
    }

}
