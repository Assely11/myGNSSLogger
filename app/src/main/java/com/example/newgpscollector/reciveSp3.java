package com.example.newgpscollector;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

public class reciveSp3 {
    private FTPClient ftpClient=new FTPClient();
    private final String server="igs.ign.fr";
    private final int port=21;
    public String fileName="";
    // public Boolean success_recived=false;
    public String log="";

    // WUM0MGXRTS_20241070000_01D_05M_ORB.SP3.gz
    public Boolean getBroadCast() {
        String result="";

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
        // String parent_path = "/pub/whu/phasebias/" + strYear + "/orbit" ;
        String parent_path="/pub/igs/products/mgex/2310";
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

                // String download_file = "WUM0MGXRTS_" + strYear+strDay + "0000_01D_05M_ORB.SP3.gz";
                /// String download_file="WUM0MGXRTS_20241070000_01D_05M_ORB.SP3.gz";
                String download_file="SHA0MGXRAP_20241050000_01D_05M_ORB.SP3.gz";
                this.fileName="WUM0MGXRTS_" + strYear+strDay + "0000_01D_05M_ORB.SP3";
                // Log.e("sp3",download_file);

                InputStream inputStream = ftpClient.retrieveFileStream(download_file);
                if (inputStream == null) {
                    log="the file is not existed!";
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
                }

                Log.e("sp3",result);

                inputStream.close();
                gzipInputStream.close();

                ftpClient.logout();
                ftpClient.disconnect();

                log=result;
                return true;
            }
        }catch (Exception e){
            log=e.toString();
            return false;
        }
    }
}
