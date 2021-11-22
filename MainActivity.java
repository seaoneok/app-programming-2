package edu.skku.map.personalassignment2;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Bitmap galleryBitmap;
    EditText editText;
    GridView gridView;
    GridView horizontal;
    GridView vertical;
    int totalNum=0;
    int checkNum=0;
    int grayColor;
    int[][] grayScale = new int[20][20];
    int ver_countBlack[][] = new int[20][10];
    int hor_countBlack[][] = new int[20][10];
    String[] ver_intToString = new String[20];
    String[] hor_intToString = new String[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.EditText);
        vertical=findViewById(R.id.vertical);
        horizontal=findViewById(R.id.horizontal);
        gridView = findViewById(R.id.gridView);

        Button search = findViewById(R.id.Search);
        Button gallery = findViewById(R.id.Gallery);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeBlack();
                makeSearchRequest();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeBlack();
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(galleryIntent,200);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();

            try {
                galleryBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            gridView.setNumColumns(20);
            new getBitmapFromGallery().execute();
        }

    }


    public void makeSearchRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://openapi.naver.com/v1/search/image?query=" + editText.getText().toString();
        StringRequest request = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String finalLink = StringToJson(response, "items", "link");
                gridView.setNumColumns(20);
                new getBitmapFromURL().execute(finalLink);
            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                params.put("X-Naver-Client-Id", "F0IWg4oreIlnGhug5h9M");
                params.put("X-Naver-Client-Secret", "2O8c_UZAea");
                return params;
            }
        };
        queue.add(request);
    }

    public String StringToJson(String json, String items, String link) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        String imageLink = "";
        try {
            jsonObject = (JSONObject) jsonParser.parse(json);
            JSONArray jsonArray = (JSONArray) jsonObject.get(items);
            JSONObject objectInArray = (JSONObject) jsonArray.get(0);
            imageLink = objectInArray.get(link).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return imageLink;
    }

    private class getBitmapFromURL extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bmp = null;
            try {
                String img_url = strings[0];
                URL url = new URL(img_url);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            initializeBoard(result);

            String[] verCount;
            verCount = verticalCount();
            ArrayList<String> ver_list = new ArrayList<>();
            for(int i = 0; i<verCount.length;i++){
                if(verCount[i] == null)
                    ver_list.add("0 ");
                else ver_list.add(verCount[i]);
            }

            String[] horCount;
            horCount = horizontalCount();
            ArrayList<String> hor_list = new ArrayList<>();
            for(int i = 0; i<horCount.length;i++){
                if(horCount[i] == null)
                    hor_list.add("0 ");
                else hor_list.add(horCount[i]);
            }

            InputCount inputLeft=new InputCount(MainActivity.this,ver_list);
            vertical.setAdapter(inputLeft);

            InputCount inputTop=new InputCount(MainActivity.this,hor_list);
            horizontal.setAdapter(inputTop);
        }
    }

    private class getBitmapFromGallery extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            return galleryBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            initializeBoard(result);

            String[] verCount;
            verCount = verticalCount();
            ArrayList<String> ver_list = new ArrayList<>();
            for(int i = 0; i<verCount.length;i++){
                if(verCount[i] == null)
                    ver_list.add("0 ");
                else ver_list.add(verCount[i]);
            }

            String[] horCount;
            horCount = horizontalCount();
            ArrayList<String> hor_list = new ArrayList<>();
            for(int i = 0; i<horCount.length;i++){
                if(horCount[i] == null)
                    hor_list.add("0 ");
                else hor_list.add(horCount[i]);
            }

            InputCount inputLeft=new InputCount(MainActivity.this,ver_list);
            vertical.setAdapter(inputLeft);

            InputCount inputTop=new InputCount(MainActivity.this,hor_list);
            horizontal.setAdapter(inputTop);
        }
    }

    private Bitmap grayScale(final Bitmap original) {
        int width, height;
        width = original.getWidth();
        height = original.getHeight();
        Bitmap bmpGrayScale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        int A, R, G, B;
        int pixel;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = original.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);
                if (gray > 128)
                    gray = 255;
                else gray = 0;
                bmpGrayScale.setPixel(x, y, Color.argb(A, gray, gray, gray));
            }
        }
        return bmpGrayScale;
    }

    private Bitmap resizeBitmap(Bitmap original) {
        int resizeWidth = 800;
        int resizeHeight = 800;
        Bitmap resize = Bitmap.createScaledBitmap(original, resizeWidth, resizeHeight, true);
        return resize;
    }

    private Bitmap[] splitImage(Bitmap original) {
        Bitmap[] game = new Bitmap[400];
        int OrH = original.getHeight();
        int OrW = original.getWidth();

        int PicW = OrW / 20;
        int PicH = OrH / 20;

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                game[i * 20 + j] = Bitmap.createBitmap(original, j * PicW, i * PicH, PicW, PicH);
                game[i * 20 + j] = Bitmap.createScaledBitmap(game[i * 20 + j], 40, 40, false);
            }
        }
        return game;
    }

    public String[] horizontalCount() {
        int cnt = 0;
        int flag = -1;
        int curr = -1;

        for (int i = 0; i < 20; i++) {
            int currentNum = 0;
            for (int j = 0; j < 20; j++) {
                if (grayScale[j][i] == 0) {
                    if(flag == 1) curr=0;
                    flag = 0;
                }
                else {
                    if (curr == 0) {
                        hor_countBlack[i][++currentNum]++;
                        totalNum++;
                        flag = 1;
                        curr = 1;
                    } else {
                        flag = 1;
                        hor_countBlack[i][currentNum]++;
                        totalNum++;
                    }
                }
            }
        }


        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                if (hor_countBlack[i][j] == 0) {
                    cnt++;
                    continue;
                } else {
                    if (hor_intToString[i] == null)
                        hor_intToString[i] = (String.valueOf(hor_countBlack[i][j])+"\n");
                    else hor_intToString[i] += (String.valueOf(hor_countBlack[i][j])+"\n");
                }
                if (cnt == 10) {
                    hor_intToString[i] = String.valueOf(0);
                }

            }
        }
        return hor_intToString;
    }


    public String[] verticalCount(){
        int cnt=0;
        int flag=-1;
        int curr = -1;
        for(int i = 0; i<20; i++){
            int currentNum=0;
            for(int j = 0; j<20; j++){
                if (grayScale[i][j] == 0) {
                    if(flag == 1) curr=0;
                    flag = 0;
                }
                else {
                    if(curr == 0) {
                        ver_countBlack[i][++currentNum]++;
                        flag = 1;
                        curr = 1;
                    }
                    else{
                        flag = 1;
                        ver_countBlack[i][currentNum]++;
                    }
                }
            }
        }

        for(int i = 0; i<20; i++){
            for(int j = 0; j<10; j++){
                if (ver_countBlack[i][j] == 0) {
                    cnt++;
                    continue;
                } else {
                    if(ver_intToString[i]==null)
                        ver_intToString[i] = (String.valueOf(ver_countBlack[i][j])+" ");
                    else ver_intToString[i] += (String.valueOf(ver_countBlack[i][j])+" ");
                }
                if(cnt==10) {
                    ver_intToString[i] = String.valueOf(0);
                }

            }
        }
        return ver_intToString;
    }



    private Bitmap chooseColor(Bitmap original){
        int avgGray = 0;
        int color;
        int width, height;
        width = original.getWidth();
        height = original.getHeight();
        Bitmap bmpGrayScale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        int R, G, B, pixel;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = original.getPixel(x, y);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);
                if (gray > 128)
                    avgGray += 255;
                else avgGray += 0;
            }
        }
        if((avgGray/(width*height))>128) {
            color = 255;
            grayColor = 0;
        }
        else {
            color = 0;
            grayColor = 1;
        }

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                bmpGrayScale.setPixel(x, y, Color.rgb(color, color, color));
            }
        }
        return bmpGrayScale;
    }

    private void initializeBlack(){
        for(int i = 0; i<20; i++){
            for(int j = 0; j<10;j++){
                ver_countBlack[i][j] = 0;
                hor_countBlack[i][j] = 0;
            }
            ver_intToString[i] = null;
            hor_intToString[i]=null;
        }
    }

    private void initializeBoard(Bitmap result) {
        final Bitmap[][] nonoGram = new Bitmap[1][1];
        Bitmap[] finalBoard = new Bitmap[400];
        Bitmap newColor;
        Bitmap realResult;
        realResult = resizeBitmap(result);
        realResult = grayScale(realResult);
        nonoGram[0] = splitImage(realResult);

        int[] checkColor = new int[400];
        for (int i = 0; i < 400; i++) {
            newColor = chooseColor(nonoGram[0][i]);
            grayScale[i / 20][i % 20] = grayColor;
            nonoGram[0][i] = newColor;
        }

        for (int i = 0; i < 400; i++) {
            finalBoard[i] = nonoGram[0][i];
            finalBoard[i].eraseColor(Color.WHITE);
        }

        Board board = new Board(MainActivity.this, finalBoard);
        gridView.setAdapter(board);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int index, long id) {
                int x = index / 20;
                int y = index % 20;
                if (grayScale[x][y] == 1) {
                    if (checkColor[x * 20 + y] == 1) {
                        finalBoard[x * 20 + y].eraseColor(Color.BLACK);
                    } else {
                        finalBoard[x * 20 + y].eraseColor(Color.BLACK);
                        checkColor[x * 20 + y] = 1;
                        checkNum++;
                    }
                    System.out.println(checkNum);
                    if (totalNum == checkNum)
                        Toast.makeText(MainActivity.this, "FINISH!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Try Again", Toast.LENGTH_SHORT
                    ).show();
                    checkNum = 0;
                    for(int i=0;i<400;i++){
                        finalBoard[i].eraseColor(Color.WHITE);
                    }
                }
            }
        });
    }
}
