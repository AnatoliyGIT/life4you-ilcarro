package com.telran.ilcarro.controller;

import com.cloudinary.Cloudinary;
import com.telran.ilcarro.model.dto.ImageDto;
import com.telran.ilcarro.model.dto.ImagesDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.*;


@CrossOrigin
@RestController
@RequestMapping
public class ImageDownloadController {

    @ApiOperation(value = "Download images")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
    })
    @PostMapping(value = "/image")
    public void downloadImageBase64(@RequestBody ImageDto image) {
        byte[] decodedImage = Base64.getDecoder().decode(image.getImage());
        File file = new File("images", LocalDate.now().toString() + ".jpeg");
        try {
            FileUtils.writeByteArrayToFile(file, decodedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "Upload images with Base64")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = String[].class),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
    })
    @PostMapping(value = "/image/upload")
    public Iterable<String> uploadPhotoBase64(@RequestBody ImagesDto images) {
        List<String> urlList = new ArrayList<>();
        final long FILE_MAX_SIZE = 1_000_000;
        final long FILE_MIN_SIZE = 500_000;
        String ROOT_PATH = "images";
        File compressedImageFile = null;
        File fileInput = null;
        Cloudinary cloudinary = new Cloudinary();
        Map config = new HashMap();
        config.put("cloud_name", "dvde7hpxw");
        config.put("api_key", "934118969476852");
        config.put("api_secret", "pOXoTrEZT5jFQ8Pvs9m1NSVuMo4");
        Map<String, Object> uploadResult;
        for (ImageDto imageDto : images.getImages()) {
            if (imageDto.getImage().isEmpty()) continue;
            byte[] decodedImage = Base64.getDecoder().decode(imageDto.getImage());
            String uuidImage = UUID.randomUUID().toString();
            File file = new File(ROOT_PATH, uuidImage + ".jpeg");

            try {
                FileUtils.writeByteArrayToFile(file, decodedImage);
                if (file.length() > FILE_MIN_SIZE) {
                    String uuid = UUID.randomUUID().toString();

                    fileInput = new File(ROOT_PATH, uuidImage + ".jpeg");
                    BufferedImage image;
                    image = ImageIO.read(fileInput);

                    compressedImageFile = new File(ROOT_PATH, uuid + ".jpeg");
                    OutputStream os = new FileOutputStream(compressedImageFile);

                    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
                    ImageWriter writer = writers.next();

                    ImageOutputStream ios = ImageIO.createImageOutputStream(os);
                    writer.setOutput(ios);

                    ImageWriteParam param = writer.getDefaultWriteParam();

                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(0.25f);
                    writer.write(null, new IIOImage(image, null, null), param);

                    os.close();
                    ios.close();
                    writer.dispose();

                    uploadResult = cloudinary.uploader().upload(compressedImageFile, config);
                    urlList.add((String) uploadResult.get("url"));

                    if (fileInput.exists()) fileInput.delete();
                    if (compressedImageFile.exists()) compressedImageFile.delete();
                } else {
                    uploadResult = cloudinary.uploader().upload(file, config);
                    urlList.add((String) uploadResult.get("url"));
                    if (file.exists()) file.delete();
                }
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Some problems)))");
            }
        }
        return urlList;
    }

    @ApiOperation(value = "Upload images with MultipartFile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 400, message = "Error! Bad request"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
    })
    @PostMapping(value = "/image/upload/v2")
    public String uploadPhoto(@RequestParam MultipartFile image) {
        final long FILE_MAX_SIZE = 1_000_000;
        final long FILE_MIN_SIZE = 500_000;
        String ROOT_PATH = "images";
        File compressedImageFile = null;
        File fileInput = null;
        Cloudinary cloudinary = new Cloudinary();
        Map config = new HashMap();
        config.put("cloud_name", "dvde7hpxw");
        config.put("api_key", "934118969476852");
        config.put("api_secret", "pOXoTrEZT5jFQ8Pvs9m1NSVuMo4");
        Map<String, Object> uploadResult;
        try {
            byte[] decodedImage = image.getBytes();
            String uuidImage = UUID.randomUUID().toString();
            File file = new File(ROOT_PATH, uuidImage + ".jpeg");
            FileUtils.writeByteArrayToFile(file, decodedImage);
            if (file.length() > FILE_MIN_SIZE) {
                String uuid = UUID.randomUUID().toString();

                fileInput = new File(ROOT_PATH, uuidImage + ".jpeg");
                BufferedImage imageBuf;
                imageBuf = ImageIO.read(fileInput);

                compressedImageFile = new File(ROOT_PATH, uuid + ".jpeg");
                OutputStream os = new FileOutputStream(compressedImageFile);

                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
                ImageWriter writer = writers.next();

                ImageOutputStream ios = ImageIO.createImageOutputStream(os);
                writer.setOutput(ios);

                ImageWriteParam param = writer.getDefaultWriteParam();

                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                if (file.length() > FILE_MAX_SIZE) {
                    param.setCompressionQuality(0.25f);
                } else {
                    param.setCompressionQuality(0.5f);
                }
                writer.write(null, new IIOImage(imageBuf, null, null), param);

                os.close();
                ios.close();
                writer.dispose();

                uploadResult = cloudinary.uploader().upload(compressedImageFile, config);

                if (fileInput.exists()) fileInput.delete();
                if (compressedImageFile.exists()) compressedImageFile.delete();
            } else {
                uploadResult = cloudinary.uploader().upload(file, config);
                if (file.exists()) file.delete();
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Some problems)))");
        }
        return (String) uploadResult.get("url");
    }
}
