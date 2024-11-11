package com.synchroteam.events;

/**
 * Created by Trident on 2/7/2017.
 */

public class TextEditImageEvent {

    public String text;
    public  int position;

    public TextEditImageEvent(String text, int position){
        this.text = text;
        this.position = position;
    }
}
