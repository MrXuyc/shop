package com.mrxuyc.shop.service.impl;

import com.google.common.collect.Lists;
import com.mrxuyc.shop.service.IFileService;
import com.mrxuyc.shop.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements IFileService {

    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);
    @Override
    public String upload(MultipartFile file, String path) {
        String fileName=file.getOriginalFilename();

        String fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName= UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件名:{}，上传路径:{}，新文件名:{}",fileName,path,uploadFileName);
        File fileDir =new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile=new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //targetFile上传到FTP服务器
            boolean uploadBoolean = FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
            //上传成功，删除upload下面的文件
            if (uploadBoolean) {
                //删除该文件
                targetFile.delete();
            }else{
                logger.warn("文件删除失败，文件路径:{},文件名:{}",path,uploadFileName);
            }
        } catch (IOException e) {
            logger.error("文件上传异常",e);
            return null;
        }
        return targetFile.getName();
    }
}
