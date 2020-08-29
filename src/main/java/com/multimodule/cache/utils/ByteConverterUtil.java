package com.multimodule.cache.utils;


import org.jsoup.SerializationException;

import java.io.*;

public class ByteConverterUtil {

    public static byte[] convertToBytes(Object o1){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(o1);
            out.flush();
            byte[] convertedBytes = bos.toByteArray();
            return convertedBytes;
        }catch (Exception e){
            throw new SerializationException("Please make sure  object is serializable");
        }finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public static Object getObject(byte[] inputBytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(inputBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return o;
        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }
}
