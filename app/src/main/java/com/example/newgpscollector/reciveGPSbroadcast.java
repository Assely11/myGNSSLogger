package com.example.newgpscollector;

//package com.example.gpslocationcollecter;

import android.content.Context;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

/**
 * written by zt
 * */

public class reciveGPSbroadcast {
    private FTPClient ftpClient=new FTPClient();
    private final String server="igs.gnsswhu.cn";
    private final int port=21;
    public String fileName="";
    // public Boolean success_recived=false;
    public String log="";
    public Boolean getBroadCast() {
        String result="";
        Boolean hasError=false;

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int Year = calendar.get(Calendar.YEAR);
        int Day = calendar.get(Calendar.DAY_OF_YEAR);
        //int Hour=calendar.get(Calendar.HOUR_OF_DAY);
        //int Min=calendar.get(Calendar.MINUTE);

        String strYear = String.valueOf(Year);
        String strDay = String.valueOf(Day);
        if (Day < 10) {
            strDay = "00" + String.valueOf(Day);
        } else if (Day < 100) {
            strDay = "0" + String.valueOf(Day);
        } else {
            strDay = String.valueOf(Day);
        }
        String parent_path = "/pub/gps/data/hourly/" + strYear + "/" + strDay;


        try{
            ftpClient.connect(server,port);
            int replyCode=ftpClient.getReplyCode();

            if(!FTPReply.isPositiveCompletion(replyCode)){
                //System.out.println("connect server failed");
                // return "connect server failed";
                log="connect server failed";
                return false;
            }else {

                ftpClient.enterLocalPassiveMode();
                ftpClient.login("ftp", "");

                // client.changeWorkingDirectory(parent_path);
                ftpClient.changeWorkingDirectory(parent_path);
//                FTPFile[] files = ftpClient.listFiles();
//                System.out.println(files.length);
//                for (FTPFile file : files) {
//                    System.out.println(file.getName());
//                }

                String download_file = "hour" + strDay + "0.24n.gz";
                this.fileName="hour" + strDay + "0.24n";

                InputStream inputStream = ftpClient.retrieveFileStream(download_file);
                if (inputStream == null) {
                    //System.out.println("the file is not existed!");
                    //return;
                    log="the file is not existed!";
                    return false;
                    //return result;
                }

                GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
                byte[] bytes = new byte[1024];
                int bytesize;

                while ((bytesize = gzipInputStream.read(bytes)) > 0) {
                    byte[] tmpByte = new byte[bytesize];
                    for (int i = 0; i < bytesize; i++) {
                        tmpByte[i] = bytes[i];
                    }
                    result+=new String(tmpByte);

                    //System.out.println(new String(tmpByte));
                }

                inputStream.close();
                gzipInputStream.close();

                ftpClient.logout();
                ftpClient.disconnect();

                //success_recived=true;
                log=result;
                return true;

                //Toast.makeText(context,"recived gps broadcast from "+download_file,Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
//            try {
//                ftpClient.logout();
//                ftpClient.disconnect();
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
            //result=e.toString();
            hasError=true;
            // success_recived=false;
        }

        if(hasError){
            try{
                ftpClient.connect(server,port);
                int replyCode=ftpClient.getReplyCode();

                if(!FTPReply.isPositiveCompletion(replyCode)){
                    //System.out.println("connect server failed");
                    // return "connect server failed";
                    log="connect server failed";
                    return false;
                }else {

                    ftpClient.enterLocalPassiveMode();
                    ftpClient.login("ftp", "");

                    // client.changeWorkingDirectory(parent_path);
                    ftpClient.changeWorkingDirectory(parent_path);
//                FTPFile[] files = ftpClient.listFiles();
//                System.out.println(files.length);
//                for (FTPFile file : files) {
//                    System.out.println(file.getName());
//                }

                    String download_file = "hour" + strDay + "0.24g.gz";
                    this.fileName="hour" + strDay + "0.24g";
                    InputStream inputStream = ftpClient.retrieveFileStream(download_file);
                    if (inputStream == null) {
                        //System.out.println("the file is not existed!");
                        //return;
                        log="the file is not existed!";
                        //return result;
                        return false;
                    }

                    GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
                    byte[] bytes = new byte[1024];
                    int bytesize;

                    while ((bytesize = gzipInputStream.read(bytes)) > 0) {
                        byte[] tmpByte = new byte[bytesize];
                        for (int i = 0; i < bytesize; i++) {
                            tmpByte[i] = bytes[i];
                        }
                        result+=new String(tmpByte);

                        //System.out.println(new String(tmpByte));
                    }

                    ftpClient.logout();
                    ftpClient.disconnect();
                    log=result;
                    //success_recived=true;
                    return true;
                    //Toast.makeText(context,"recived gps broadcast from "+download_file,Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
//            try {
//                ftpClient.logout();
//                ftpClient.disconnect();
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
                log=e.toString();
                //success_recived=false;
                return false;
                //hasError=true;
            }
        }



        return true;
    }

    public String toString(){
        return "author: Zhou.T email:zhoutaowhu@whu.edu.cn, gpsBrodCastReciver";
    }
}
