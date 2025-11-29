package com.example.projectapi.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class StorageService {

    private final AmazonS3 s3Client;

    @Value("${b2.bucket-name}")
    private String bucketName;

    @Value("${b2.endpoint}")
    private String endpoint;

    public StorageService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) {
        // 1. Generar nombre único (UUID) para evitar que se sobrescriban archivos con el mismo nombre
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            // 2. Preparar metadatos (tamaño y tipo de archivo)
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // 3. Subir el archivo a Backblaze
            s3Client.putObject(new PutObjectRequest(bucketName, uniqueFileName, file.getInputStream(), metadata));

            // 4. Construir la URL Pública manualmente
            // Backblaze tiene este formato: https://<endpoint>/file/<bucketName>/<fileName>

            // Limpiamos el endpoint por si pusiste 'https://' en el properties para evitar duplicarlo
            String cleanEndpoint = endpoint.replace("https://", "").replace("http://", "");

            return "https://" + cleanEndpoint + "/" + bucketName + "/" + uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("Error al subir archivo a Backblaze", e);
        }
    }
}