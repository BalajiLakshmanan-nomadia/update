package com.codescanner;

import com.google.zxing.Result;

/**
 * <p>Encapsulates the result of decoding a barcode within an image.</p>
 *
 * @author Sean Owen
 */
public final class ResultNew {


   private final byte[] image;
   Result result;
    int imageWidth;
    int imageHeight;

    public ResultNew(Result result, byte[] mImage, int imageWidth, int imageHeight) {
        this.result=result;
        this.image=mImage;
        this .imageWidth=imageWidth;
        this.imageHeight=imageHeight;
    }

    /**
    * @return raw text encoded by the barcode
    */
   public Result getResult() {
      return result;
   }

   public byte[] getScanImage() {
      return image;
   }
    public int getWidthImage() {
        return imageWidth;
    }
    public int getHeightImage() {
        return imageHeight;
    }
}

