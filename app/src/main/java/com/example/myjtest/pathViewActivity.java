package com.example.myjtest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class pathViewActivity extends AppCompatActivity {
    SubsamplingScaleImageView imageView;
    GestureDetector gestureDetector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_view);
        imageView = findViewById(R.id.imageView);
        imageView.setImage(ImageSource.asset("map.png"));
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() { // gesture 디텍팅으로 지하철 위치 읽기
            @Override
            public boolean onSingleTapUp(MotionEvent ev) {
                if (imageView.isReady()) {
                    PointF sCoord = imageView.viewToSourceCoord(ev.getX(), ev.getY());
                    int x_cor = (int) sCoord.x;
                    int y_cor = (int) sCoord.y;
                    Log.d("x", x_cor + "");
                    Log.d("y", y_cor + "");

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) {
                                    int name = jsonObject.getInt("nameData");
                                    int x1 = jsonObject.getInt("X1");
                                    int y1 = jsonObject.getInt("Y1");
                                    int x2 = jsonObject.getInt("X2");
                                    int y2 = jsonObject.getInt("Y2");

                                    Station st = new Station(name,x1,y1,x2,y2);
                                    if(st.isClicked(x_cor,y_cor))
                                        Toast.makeText(getApplicationContext(), name + "클릭!!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "값 가져오기 실패", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    };
                    DataRequest loginRequest = new DataRequest(101 + "", responseListener);
                    RequestQueue queue = Volley.newRequestQueue(pathViewActivity.this);
                    queue.add(loginRequest);
                }
                return super.onSingleTapUp(ev);
            }
        });
    }
}