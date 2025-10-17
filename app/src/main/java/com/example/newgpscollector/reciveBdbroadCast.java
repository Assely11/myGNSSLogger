package com.example.newgpscollector;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;


// ABPO00MDG_R_20241062300_01H_MN.rnx.gz
// ABPO00MDG_R_20241070800_01H_MN.rnx.gz
// ABMF00GLP_R_20241070300_01H_30S_MO.crx.gz
// AREG00PER_R_20241071300_15M_MN.rnx.gz
public class reciveBdbroadCast {
    private FTPClient ftpClient=new FTPClient();
    private final String server="igs.ign.fr";
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
        int Hour=calendar.get(Calendar.HOUR_OF_DAY);
        int Min=calendar.get(Calendar.MINUTE);
        // Log.e("debug",String.valueOf(Hour));

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
        String parent_path = "/pub/igs/data/highrate/" + strYear + "/" + strDay;

        String strHour=String.valueOf(Hour);
        if(Hour <10){
            strHour="0"+String.valueOf(Hour);
        }

        String strMin=String.valueOf(Min);
        if(Min <10){
            strMin="0"+String.valueOf(Min);
        }

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

                String download_file = "AREG00PER_R_" +strYear +strDay+strHour+strMin + "_15M_MN.rnx.gz";
                // String download_file="ABPO00MDG_R_20241070800_01H_MN.rnx.gz";
                this.fileName="AREG00PER_R_" +strYear +strDay+strHour+strMin + "_15M_MN.rnx";

                // Log.e("bd",download_file);
                InputStream inputStream = ftpClient.retrieveFileStream(download_file);
                if (inputStream == null) {
                    //System.out.println("the file is not existed!");
                    //return;
                    log="the file is not existed!";
                    // Log.e("bd","the file is not existed!");
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
                    // Log.e("bd",result);
                    //System.out.println(new String(tmpByte));
                }

                inputStream.close();
                gzipInputStream.close();

                ftpClient.logout();
                ftpClient.disconnect();

                //success_recived=true;
                log=result;
                return true;
            }
        }catch (Exception e){
            log=e.toString();
            return false;
        }

    }

}
