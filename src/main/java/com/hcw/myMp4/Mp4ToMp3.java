package com.hcw.myMp4;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;

public class Mp4ToMp3 {
    /**
     * 视频文件转音频文件
     * @param videoPath
     * @param audioPath
     * return true or false
     */
    public static boolean videoToAudio(String videoPath, String audioPath){
        Long start = System.currentTimeMillis();
        File fileMp4 = new File(videoPath);
        File fileMp3 = new File(audioPath);



        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        audio.setBitRate(128000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setDecodingThreads(8);
        attrs.setEncodingThreads(8);
        attrs.setOutputFormat("mp3");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();
        MultimediaObject mediaObject = new MultimediaObject(fileMp4);
        try{
            encoder.encode(mediaObject,fileMp3,attrs);
            Long end = System.currentTimeMillis();
//            Log.info("File MP4 convertito MP3");
            System.out.println("File MP4 convertito MP3 in " + (end - start) + " ms");
            return true;
        }catch (Exception e){
            Long end = System.currentTimeMillis();
//            Log.error("File non convertito");
//            Log.error(e.getMessage());
            System.out.println("File non convertito");
            return false;
        }
    }

}
