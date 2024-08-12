package com.example.shoppingmall.service.product;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        // 프론트엔드의 이미지 저장 경로로 설정
        this.fileStorageLocation = Paths.get("frontshoppingmall/public/images/products")
                .toAbsolutePath()
                .normalize();

        // 경로가 없으면 디렉토리 생성
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // 고유한 파일명 생성
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            // 파일 저장 경로 설정
            Path targetLocation = this.fileStorageLocation.resolve(fileName);

            // 파일 저장
            file.transferTo(targetLocation.toFile());

            // 프론트엔드에서 접근 가능한 경로 반환
            return "images/products/" + fileName; // 여기서 상대 경로로 반환
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file " + fileName, ex);
        }
    }
}

