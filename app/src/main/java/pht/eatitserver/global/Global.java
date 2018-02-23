package pht.eatitserver.global;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Canvas;
import android.graphics.Paint;
import pht.eatitserver.model.Request;
import pht.eatitserver.model.User;
import pht.eatitserver.remote.FCMService;
import pht.eatitserver.remote.MapService;
import pht.eatitserver.remote.RetrofitClient;

public class Global {

    public static User activeUser;
    public static Request currentRequest;
    public static final int PICK_IMAGE_REQUEST = 71;
    public static final String BASE_MAP_URL = "https://maps.googleapis.com";
    public static final String BASE_FCM_URL = "https://fcm.googleapis.com";

    public static String convertCodeToStatus(String code){
        if(code.equals("0")){
            return "Placed";
        }
        else if(code.equals("1")){
            return "On my way";
        }
        else {
            return "Shipped";
        }
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        float scaleX = newWidth / (float)bitmap.getWidth();
        float scaleY = newHeight / (float)bitmap.getHeight();
        float pivotX = 0, pivotY = 0;

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(newBitmap);
        canvas.setMatrix(matrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return  newBitmap;
    }

    public static FCMService getFCMAPI(){
        return RetrofitClient.getFCMClient(BASE_FCM_URL).create(FCMService.class);
    }

    public static MapService getMapAPI(){
        return RetrofitClient.getMapClient(BASE_MAP_URL).create(MapService.class);
    }
}