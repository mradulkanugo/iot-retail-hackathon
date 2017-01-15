package com.hackathone.iotretail.warehouseserver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by amitp on 1/15/17.
 */
public class TrackView extends SurfaceView implements Runnable{
    int speed=5000;
    ArrayList<ItemOnTrack> itemsOnTrack=new ArrayList<>();
    Thread thread=new Thread(this);
    boolean torun=true;

    Paint[] productPaints=new Paint[4];
    {
        for(int i=0;i<productPaints.length;i++){
            productPaints[i]=new Paint();
            productPaints[i].setStyle(Paint.Style.FILL);
        }
        productPaints[0].setARGB(255,0,255,0);
        productPaints[1].setARGB(255,0,0,255);
        productPaints[2].setARGB(255,0,255,255);
        productPaints[3].setARGB(255,255,255,0);
    }


    public TrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        thread.start();
    }

    public TrackView(Context context) {
        super(context);
        thread.start();
    }

    public TrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        thread.start();
    }

    @Override
    public void run() {
        
        while(torun){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(int counter=0;counter<itemsOnTrack.size();counter++){
                itemsOnTrack.get(counter).positionOnTrack-=(50.0*100.0/speed);
                if(itemsOnTrack.get(counter).positionOnTrack<0){
                    itemsOnTrack.remove(counter);
                    counter--;
                }
                drawItemsOnTrack();
            }
        }


    }
     void drawItemsOnTrack(){
         SurfaceHolder holder=this.getHolder();
         Canvas canvas=holder.lockCanvas();
         int height=canvas.getHeight();
         int width=canvas.getWidth();

         canvas.drawARGB(255,255,255,255);
         canvas.drawARGB(20,0,0,255);
         for(int counter=0;counter<itemsOnTrack.size();counter++) {
             canvas.drawRect(width / 4,( ((100-itemsOnTrack.get(counter).positionOnTrack )- 5) * height)/100, (width * 3) / 4, (((100-itemsOnTrack.get(counter).positionOnTrack) + 5) * height)/100, productPaints[itemsOnTrack.get(counter).itemId-1]);
         }
         holder.unlockCanvasAndPost(canvas);
     }
    public class ItemOnTrack{
        float positionOnTrack=100;
        int itemId=0;
        public ItemOnTrack(float positionOnTrack,int itemId){
            this.itemId=itemId;
            this.positionOnTrack=positionOnTrack;
        }
    }
    void addItemOnTrack(float position,int id){
        itemsOnTrack.add(new ItemOnTrack(position,id));
    }
}
