

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;


class ChangeContrast {
    BufferedImage biSrc, biDest, bi; 
    RescaleOp rescale;
    float scaleFactor = 1.0f;
    float offset = 10;
    boolean brighten, contrastInc;
    
    public ChangeContrast(boolean brighten){
        this.brighten = true;
    }
    public ChangeContrast(BufferedImage biSrc,BufferedImage biDest) {
       
        this.biSrc = biSrc;
        this.biDest = biDest;
        this.bi=this.biSrc;

    }

   public void changeOffSet() {
        if (brighten) {
            if (offset < 255)
               offset = offset+5.0f;
        }
        else {
            if (offset > 0)
               offset = offset-5.0f;
        }
    }

    public void changeScaleFactor() {
        if (contrastInc) {
            if (scaleFactor < 2)
                scaleFactor = scaleFactor+0.1f;
        }
        else {
            if (scaleFactor > 0)
                scaleFactor = scaleFactor-0.1f;
        }
    }

    public BufferedImage rescale() {
        rescale = new RescaleOp(scaleFactor, offset, null);
        rescale.filter(biSrc, biDest);
        bi = biDest;
        return bi;
    }

}