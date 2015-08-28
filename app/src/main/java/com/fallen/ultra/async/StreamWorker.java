package com.fallen.ultra.async;


import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.fallen.ultra.com.fallen.ultra.model.TempAsyncStatus;
import com.fallen.ultra.services.UltraPlayerService;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

/**
* Created by Nolwe on 13.03.2015.
*/
public class StreamWorker implements Runnable {
    Socket client;
    HttpURLConnection uc;
    OutputStream emulatingStream;
    boolean isCanceledManualy;
    private boolean isBuffered;
    Handler universalUIHandler;
    String currentQuality;

    public void setCancelLoad ()
    {
        isCanceledManualy = true;
    }

    public StreamWorker(Handler universalUIHandler, String currentQuality) {
        this.currentQuality = currentQuality;
        this.universalUIHandler = universalUIHandler;
    }

    @Override
    public void run() {
        doLoadWork();
    }

    private void doLoadWork() {
        URL url;
        isBuffered = false;
        int metaInt;
        int bytePreIntSum = 0;
        int bufferedSum = 0;
        int bufferBeforePlayback = Params.BUFFER_FOR_PLAYER_IN_BYTES;
        TempAsyncStatus tempAsyncStatus = new TempAsyncStatus();
        ServerSocket serverSocket = null;

        File fileToWrite;
        FileOutputStream fileOutputStream = null;
        OutputStream out = null;
        InputStream radioStream = null;
        try {

            url = new URL(currentQuality);
            tempAsyncStatus = new TempAsyncStatus();
            tempAsyncStatus.setStatus(Params.STATUS_CONNECTING);

            publishProgress(tempAsyncStatus);

            uc = getUrlConnection(url);
            uc.setUseCaches(true);
            uc.setConnectTimeout(Params.CONNECTION_TIMEOUT);
            uc.setReadTimeout(5000);
            uc.setConnectTimeout(5000);
            uc.connect();
            metaInt = getMetInt(uc);
            UtilsUltra.printLog("metaInt " + metaInt, null, 0);
            if (bufferBeforePlayback < metaInt + Params.MAX_METAINT_VALUE)
                bufferBeforePlayback = metaInt + Params.MAX_METAINT_VALUE;
            UtilsUltra.printLog("bufferBeforePlayback " + bufferBeforePlayback, null, 0);
            radioStream = uc.getInputStream();
            byte[] buffer = new byte[Params.DEFAULT_INPUTSTREAM_BUFFER_BYTES];
            int bytesRealyRead;
            int byteToRead = 1024;
            try {
                serverSocket = new ServerSocket(
                        Integer.parseInt(Params.SOCKET_PORT));
                serverSocket.setSoTimeout(5000);
                client = new Socket();
                client.setKeepAlive(true);
                client.setSoTimeout(5000);
                UtilsUltra.printLog("socket created");
                tempAsyncStatus = new TempAsyncStatus();
                tempAsyncStatus.setStatus(Params.STATUS_SOCKET_CREATING);
               publishProgress(tempAsyncStatus);
                UtilsUltra.printLog("updateAsyncStatusObject status, and waiting for mediaplayer connect");
                client = serverSocket.accept();
                emulatingStream = client.getOutputStream();
                emulatingStream = configureOutputstream(emulatingStream);
            } catch (NumberFormatException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            tempAsyncStatus = new TempAsyncStatus();
            tempAsyncStatus.setStatus(Params.STATUS_BUFFERING);
            publishProgress(tempAsyncStatus);

            while ((bytesRealyRead = radioStream.read(buffer, 0, byteToRead)) != -1
                    && !isCanceledManualy) {

                //UtilsUltra.printLog("radioStream avai " +radioStream.available());
                if (isCanceledManualy) {
                    UtilsUltra.printLog("cleaning sockets");
                    if (serverSocket != null)
                        serverSocket.close();
                    if (client!=null)
                        client.close();
                    if (emulatingStream!=null) {
                        emulatingStream.flush();
                        emulatingStream.close();
                        emulatingStream = null;
                    }
                    break;
                }
                if (metaInt != Params.NO_METAINT)
                    bytePreIntSum += bytesRealyRead;
                if (!isBuffered) {
                    bufferedSum += bytesRealyRead;
                    if (bufferedSum > bufferBeforePlayback)
                    {
                        isBuffered = true;
                        tempAsyncStatus = new TempAsyncStatus();
                        tempAsyncStatus.setStatus(Params.STATUS_NONE);
                        System.out.println("send NONE status");
                        publishProgress(tempAsyncStatus);
                    }
                }
                if (bytePreIntSum == metaInt && metaInt != Params.NO_METAINT) {
                    int metadataLenght = radioStream.read();
                    if (metadataLenght > 0) {
                        int byteSumMetadata = metadataLenght * 16;
                        int realyReadMetaData;
                        byte[] metaDataBuffer = new byte[metadataLenght * 16];
                        //String str = new String(metaDataBuffer, "UTF-8");
                        StringBuilder str = new StringBuilder();
                        int realyReadMetaDataSum = 0;
                        while (realyReadMetaDataSum  < byteSumMetadata && !isCanceledManualy) {
                            realyReadMetaData = radioStream.read(metaDataBuffer, 0, metaDataBuffer.length);
                            realyReadMetaDataSum = realyReadMetaDataSum + realyReadMetaData;
                            String tempString  = new String(metaDataBuffer, "UTF-8");
                            if (realyReadMetaDataSum < byteSumMetadata )
                                metaDataBuffer = new byte [byteSumMetadata - realyReadMetaDataSum];
                            str.append(tempString);
                        }
                        ContentValues metadataParsed = UtilsUltra.createBundleWithMetadata(str.toString());
                        String artist = metadataParsed.getAsString(Params.TRACK_ARTIST_KEY);
                        String track = metadataParsed.getAsString(Params.TRACK_SONG_KEY);
                        fileToWrite = new File (createOutputFileByTitle(artist, track));
                        if (fileOutputStream!=null)
                            fileOutputStream.close();
                        //fileOutputStream = new FileOutputStream(fileToWrite);


                        tempAsyncStatus = new TempAsyncStatus();
                        tempAsyncStatus.setStatus(Params.STATUS_NEW_TITLE, artist, track);
                        System.out.println("send new title status");

                        publishProgress(tempAsyncStatus);

                    }
                    bytePreIntSum = 0;
                    byteToRead = Params.DEFAULT_INPUTSTREAM_BUFFER_BYTES;
                }
                if (emulatingStream!=null)
                    emulatingStream.write(buffer, 0, bytesRealyRead);
//               if (fileOutputStream!=null)
//                    fileOutputStream.write(buffer, 0, bytesRealyRead);


                if (bytePreIntSum + Params.DEFAULT_INPUTSTREAM_BUFFER_BYTES > metaInt  && metaInt != Params.NO_METAINT) {
                    byteToRead = metaInt - bytePreIntSum;

                }

            }

        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        UtilsUltra.printLog("Server rerturn -1, closing socket", null, Log.ERROR);
        tempAsyncStatus.setStatus(Params.STATUS_STREAM_ENDS);
       publishProgress(tempAsyncStatus);
        try {

            if (client !=null)
                client.close();
            if (out != null)
                out.close();
            if (serverSocket != null)
                serverSocket.close();

            if (radioStream !=null)
                radioStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
        if (isCanceledManualy)
        {
            tempAsyncStatus = new TempAsyncStatus();
            tempAsyncStatus.setStatus(Params.STATUS_CANCELED);
            publishProgress(tempAsyncStatus);
        }
    }

    private void publishProgress(TempAsyncStatus tempAsyncStatus) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("status", tempAsyncStatus);
        universalUIHandler.sendMessage(UtilsUltra.createMessage(0,bundle));
    }

    private String createOutputFileByTitle(String artist, String title) {
        // TODO Auto-generated method stub
        String fileName = artist + " " + title;



        File checkFileExists = new File (Params.SAVE + fileName+".mp3");
        int fileIndex =0;
        while (checkFileExists.exists())
        {

            fileIndex++;
            checkFileExists = new File (Params.SAVE + fileName+"("+String.valueOf(fileIndex)+").mp3");
        }
        String fileToPlay = Params.SAVE + checkFileExists.getName();
        //System.out.println ("constructed name = " + fileToPlay);
        return fileToPlay;
    }

    private HttpURLConnection getUrlConnection(URL url) {
        HttpURLConnection urlConnection = null;
        try {
            UtilsUltra.printLog("opening connection " + url, null, 0);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.addRequestProperty("Icy-MetaData", "1");
            urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6");
            urlConnection.addRequestProperty("Content-Type", "audio/mpeg");
        } catch (IOException e) {
            e.printStackTrace();
            UtilsUltra.printLog("failed connection ", null, Log.ERROR);
        }
        UtilsUltra.printLog("opening connection done " + url, null, 0);
        return urlConnection;
    }

    private OutputStream configureOutputstream(OutputStream emulatingStream) {
        String responseCode = ("HTTP/1.1 200 OK\r\n");
        String cache = "Cache-Control: no-cache\r\n";
        String contentType = ("Content-Type: audio/mpeg\r\n\r\n");
        String responseFakeString = responseCode + cache + contentType;
        try {
            emulatingStream.write(responseFakeString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emulatingStream;
    }

    public  int getMetInt(HttpURLConnection uc) {
        // TODO Auto-generated method stub
        int metaInt = 0;
        try {
            metaInt = Integer.parseInt(uc.getHeaderField("icy-metaint"));
            UtilsUltra.printLog("ICY retrieved " + metaInt, null, 0);
            if (metaInt == 0)
                return Params.NO_METAINT;
        } catch (Exception e) {
            UtilsUltra.printLog("ICY exeprion occured " + metaInt, null,
                    Log.WARN);
            e.printStackTrace();
            return Params.NO_METAINT;
        }
        return metaInt;
    }
}
