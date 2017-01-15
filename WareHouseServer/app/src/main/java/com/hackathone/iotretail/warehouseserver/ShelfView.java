package com.hackathone.iotretail.warehouseserver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by amitp on 1/14/17.
 */
public class ShelfView extends SurfaceView {
    static int ALIGNED_Left=0;
    static int ALIGNED_RIGHT=1;
    int shelfNumber=0;



    public ShelfView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShelfView(Context context) {
        super(context);
    }

    public ShelfView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    public void setIteminShelf(int numberOfItem,int totalSpace,int aligned){
        SurfaceHolder holder=this.getHolder();
        Canvas canvas=holder.lockCanvas();
        int height=canvas.getHeight();
        int width=canvas.getWidth();
        Paint[] productPaints=new Paint[4];
        for(int i=0;i<productPaints.length;i++){
            productPaints[i]=new Paint();
            productPaints[i].setStyle(Paint.Style.FILL);
        }
        productPaints[0].setARGB(255,0,255,0);
        productPaints[1].setARGB(255,0,0,255);
        productPaints[2].setARGB(255,0,255,255);
        productPaints[3].setARGB(255,255,255,0);
        Paint redPaint=new Paint();
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setARGB(255,255,0,0);
        canvas.drawARGB(255,255,255,255);
        if(aligned==0){
            canvas.drawRect(0,0,((totalSpace-numberOfItem)*width)/totalSpace,height,redPaint);
            canvas.drawRect(((totalSpace-numberOfItem)*width)/totalSpace,0,width,height,productPaints[shelfNumber-1]);
        }else{
            canvas.drawRect(0,0,((numberOfItem)*width)/totalSpace,height,productPaints[shelfNumber-1]);
            canvas.drawRect(((numberOfItem)*width)/totalSpace,0,width,height,redPaint);
        }
        holder.unlockCanvasAndPost(canvas);
    }
    public void setShelfNumber(int shelfNumber){
        this.shelfNumber=shelfNumber;
    }
}
