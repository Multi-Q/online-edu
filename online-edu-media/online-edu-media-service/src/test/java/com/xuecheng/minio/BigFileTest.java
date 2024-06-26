package com.xuecheng.minio;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author QRH
 * @date 2023/7/1 16:35
 * @description
 */
@SpringBootTest
public class BigFileTest {

    //文件分块
   @Test
   public void testChunk() throws IOException {
       //找到源文件
       File sourceFile = new File("E:\\123\\287cdd91c5d444e0752b626cbd95b41c.mp4");
       //分块文件存储路径
       String chunkFilePath="E:\\123";
       //分块大小
       int chunkSize=1024*1024*1;
       //分块文件的个数
       int chunkNum=(int)Math.ceil(sourceFile.length()*1.0/chunkSize);
       //使用流从文件读数据，向分块文件中写数据
       RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");
       //缓冲区
       byte[] bytes = new byte[1024];

       for (int i = 0; i < chunkNum; i++) {
           File chunkFile = new File(chunkFilePath + i);
           //分块文件写入流
           RandomAccessFile raf_rw = new RandomAccessFile(chunkFile, "rw");
           int len=-1;
           while((len=raf_r.read(bytes))!=-1){
               raf_rw.write(bytes,0,len);
               if (chunkFile.length()>=chunkSize){
                   break;
               }
           }
           raf_rw.close();
       }
       raf_r.close();
   }

//   文件合并
    @Test
    public void testMerge() throws IOException {
        //块文件目录
        File chunkFolder = new File("E:\\123");
        //源文件
        File sourceFile = new File("E:\\123\\287cdd91c5d444e0752b626cbd95b41c.mp4");
        //合并后的文件
        File mergeFile = new File("E:\\123\\5678.mp4");

        //取出所有分块文件
        File[] files = chunkFolder.listFiles();
        //将数组转成list
        List<File> fileList = Arrays.asList(files);

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName())-Integer.parseInt(o2.getName());
            }
        });
        //向合并文件写的流
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");
        //缓冲区
        byte[] bytes = new byte[1024];
        //遍历分块文件，向分块文件写
        fileList.forEach(file -> {
            try {
                //读分块的流
                RandomAccessFile raf_r = new RandomAccessFile(file, "r");
                int len=-1;
                while ((len=raf_r.read(bytes))!=-1){
                    raf_rw.write(bytes,0,len);
                }
                raf_r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        raf_rw.close();
        //合并文件后对合并的文件进行校验
        FileInputStream fileInputStream_merge = new FileInputStream(mergeFile);
        FileInputStream fileInputStream_source = new FileInputStream(sourceFile);
        String md5_merge = DigestUtils.md5Hex(fileInputStream_merge);
        String md5_source = DigestUtils.md5Hex(fileInputStream_source);
        if (md5_merge.equals(md5_source)){
            System.out.println("合并成功");
        }
    }

}
