package com.example.newgpscollector;

import android.os.Environment;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

public class ReciveBDNavigation {
    private String tmpFilePath="";
    private String storageFilePath="";

//    private File tmpFileDirect;
//    private File tmpStorageDirect;

    public ReciveBDNavigation(String tmpPath,String storagePath){
        this.tmpFilePath=tmpPath;
        this.storageFilePath=storagePath;

//        this.tmpFileDirect=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),tmpPath);
//        this.tmpStorageDirect=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),storagePath);
//
//        try{
//            if(!tmpFileDirect.exists()){
//                tmpFileDirect.mkdir();
//            }
//        }catch (Exception e){}
//        try{
//            if(!tmpStorageDirect.exists()){
//                tmpStorageDirect.mkdir();
//            }
//        }catch (Exception e){}

        this.recivedGZipFile=tmpPath+"/bd_tmpFile.gz";
    }


    private FTPClient ftpClient=new FTPClient();
    private final String server="igs.ign.fr";

    private final int port=21;
    private final String user="ftp";
    private final String passwd="";

    private String recivedGZipFile= null;
    public String log;
    public String fileName;

    public Boolean downloadFile(){
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int Year=calendar.get(Calendar.YEAR);
        int Day=calendar.get(Calendar.DAY_OF_YEAR);
        int Hour=calendar.get(Calendar.HOUR_OF_DAY);
        int Min=calendar.get(Calendar.MINUTE);

        Hour=Hour-1;
        if(Hour < 0){
            Hour+=24;
            Day-=1;
        }

        if(Day <0){
            Year-=1;
            if(Math.floorMod(Year,4)==0||Math.floorMod(Year,100)==0){
                Day+=366;
            }else{
                Day+=365;
            }
        }

        Min=Min-Math.floorMod(Min,15);

        String strYear = String.valueOf(Year);
        String strDay = String.valueOf(Day);
        if (Day < 10) {
            strDay = "00" + String.valueOf(Day);
        } else if (Day < 100) {
            strDay = "0" + String.valueOf(Day);
        } else {
            strDay = String.valueOf(Day);
        }

        String strHour=String.valueOf(Hour);
        if(Hour <10){
            strHour="0"+String.valueOf(Hour);
        }

        String strMin=String.valueOf(Min);
        if(Min <10){
            strMin="0"+String.valueOf(Min);
        }

        try {
            ftpClient.connect(server,port);
            int replyCode=ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(replyCode)){
                log="connect failed";
                return false;
            }else{
                ftpClient.enterLocalPassiveMode();;
                ftpClient.login(user,passwd);

                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);

                String parent_path="/pub/igs/data/highrate/" + strYear + "/" + strDay;

                ftpClient.changeWorkingDirectory(parent_path);

                String downloadFile="AREG00PER_R_" +strYear +strDay+strHour+strMin + "_15M_MN.rnx.gz";

                String tmpDownloadFileName=this.recivedGZipFile;
                File tmpFile=new File(tmpDownloadFileName);
                if(!tmpFile.exists()){
                    tmpFile.createNewFile();
                }else{
                    tmpFile.delete();
                    tmpFile.createNewFile();
                }
                FileOutputStream fileOutputStream=new FileOutputStream(tmpFile,false);

                if(ftpClient.retrieveFile(downloadFile,fileOutputStream)){
                    recivedGZipFile=tmpDownloadFileName;
                    fileName="AREG00PER_R_" +strYear +strDay+strHour+strMin + "_15M_MN.rnx";
                    return true;
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            log=e.toString();
            return false;
            //throw new RuntimeException(e);
        }
        return false;
    }

    public Boolean uZipFile(){
        String result="";
        try {
            FileInputStream fileInputStream=new FileInputStream(recivedGZipFile);
            try {
                GZIPInputStream gzipInputStream=new GZIPInputStream(fileInputStream);
                byte[] bytes=new byte[1024];
                int byteSize;
                while ((byteSize=gzipInputStream.read(bytes))>0){
                    byte[] tmpByte=new byte[byteSize];
                    for(int i=0;i<byteSize;i++){
                        tmpByte[i]=bytes[i];
                    }
                    result+=new String(tmpByte);
                }
                log=result;
                gzipInputStream.close();
                fileInputStream.close();

                // log=result;
                return true;
            } catch (IOException e) {
                log=e.toString();
                //throw new RuntimeException(e);
                return false;
            }

        } catch (FileNotFoundException e) {
            //
            log=e.toString();
            return false;
        }
        //return true;
    }

    public Boolean updateLogger(){
        if(downloadFile()){
            return uZipFile();
        }

        return false;
    }

    public Boolean initLogger(){
        File file=new File(recivedGZipFile);
        if(file.exists()){
            return uZipFile();
        }
        return false;
    }
}
