package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.FileInfo;
import com.developlife.reviewtwits.entity.FileManager;
import com.developlife.reviewtwits.repository.FileInfoRepository;
import com.developlife.reviewtwits.repository.FileManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStoreService {

    @Value("${file.dir}")
    private String fileDir;

    private final FileInfoRepository fileInfoRepository;
    private final FileManagerRepository fileManagerRepository;

    @Transactional
    public FileInfo storeFile(MultipartFile multipartFile) throws IOException{
        if(multipartFile.isEmpty()){
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFilename = createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storeFilename)));
        FileInfo file = new FileInfo(getFullPath(storeFilename),storeFilename,originalFilename);
        fileInfoRepository.save(file);
        return file;
    }

    public List<FileInfo> storeFiles(List<MultipartFile> multipartFiles, Long referenceID, String referenceType) throws IOException {
        List<FileInfo> storeFileResult = new ArrayList<>();
        List<FileManager> fileManagerList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()){
                FileInfo fileInfo = storeFile(multipartFile);
                storeFileResult.add(fileInfo);
                fileManagerList.add(new FileManager(fileInfo.getFileID(),referenceID,referenceType));
            }
        }
        fileManagerRepository.saveAll(fileManagerList);

        return storeFileResult;
    }

    public String getFullPath(String filename){
        return fileDir + filename;
    }


    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int position = originalFilename.lastIndexOf(".");
        return originalFilename.substring(position + 1);
    }
}
