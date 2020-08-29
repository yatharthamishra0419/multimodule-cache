package com.multimodule.cache.annotations;

import java.io.Serializable;

public class Counter implements Serializable {

    int count=0;

    String sampleAnnotationKey="hello";
    public void increment(){
        this.count++;
    }

    public int getCount() {
        return count;
    }

    public String getSampleAnnotationKey() {
        return sampleAnnotationKey;
    }
}
