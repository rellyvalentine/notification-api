package com.valentine.demo.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfilePictureService {

    @Autowired
    ProfilePictureRepository imageRepo;

    public void save(ProfilePicture s){
        imageRepo.save(s);
    }

    public String getPfpByUserId(long id){
        String image = imageRepo.getProfilePictureById(id).getLocation();
        if(image.isEmpty()){
            return getDefaultPfp();
        }
        return image;
    }

    public String getDefaultPfp(){
        return "default-images/default-profile.png";
    }

    public List<ProfilePicture> getAllImages(){
        return imageRepo.findAll();
    }

    public int getImageNumber(){
        return getAllImages().size();
    }

}
