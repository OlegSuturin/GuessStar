package com.example.guessstar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private String urlBase = "http://www.spletnik.ru/";
    private String urlPage = "http://www.spletnik.ru/ratings";

    private ArrayList<Star> arrayListStar;
    private String resultHTML;
    private ArrayList<String> stringBuf;
    Bitmap image = null;

    ImageView imageViewStar;
    ListView listViewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageViewStar = findViewById(R.id.imageViewStar);
        listViewName = findViewById(R.id.listViewName);

        arrayListStar = new ArrayList<>();
        stringBuf = new ArrayList<>();

        DownloadTask task = new DownloadTask();
        try {
            resultHTML = task.execute(urlPage).get(); // выкачиваем HTML-код
            //Log.i("!@#", resultHTML);
            Pattern patternString = Pattern.compile("<img src=\"/thumb/200x200/(.*?)\"\n");  //вытягиваем буферные  строки содержащие полезную информацию сгруппированную
            Matcher matcherString = patternString.matcher(resultHTML);

            String result;
            while (matcherString.find()) {
                result = matcherString.group();
                stringBuf.add(result);
                 //Log.i("!@#", result);
            }

            Pattern patternImg = Pattern.compile("<img src=\"(.*?)\"");  // вытягиваем адреса картинок
            Matcher matcherImg;
            String addresImg = null;
            Pattern patternName = Pattern.compile("alt=\"(.*?)\"");     // вытягиваем имена звезд
            Matcher matcherName;
            String name;




            for (String buf : stringBuf) {
                matcherImg = patternImg.matcher(buf);
                matcherName = patternName.matcher(buf);
                if (matcherImg.find() && matcherName.find()) {
                    addresImg = matcherImg.group(1);
                    name = matcherName.group(1);


                    arrayListStar.add(new Star(name, urlBase + addresImg));   // создаем список звезд
                   // Log.i("!@#", name + "   " + addresImg);
                }
            }
            DownloadTaskImg taskImg;    //выкачиваем картинки

            for(int i=0; i<arrayListStar.size(); i++) {

                Log.i("!@#", arrayListStar.get(i).getName() +" "+ arrayListStar.get(i).getAddressImg());

                taskImg = new DownloadTaskImg();
                image = taskImg.execute(arrayListStar.get(i).getAddressImg()).get();
                if (image != null) arrayListStar.get(i).setBitmap(image);     //сохраняем их в объект star
            }

            Log.i("!@#", "Всего Звезд: " + arrayListStar.size());

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //for(int i=0; i<arrayListStar.size(); i++)
        imageViewStar.setImageBitmap(arrayListStar.get(8).getBitmap());

        ArrayAdapter<Star> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayListStar);



    }

    private static class DownloadTaskImg extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... strings) {
            Log.i("!@#", strings[0]);
            URL url = null;
            HttpURLConnection urlConnection = null;
            Bitmap bitmap;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }


            return null;
        }
    }


    private static class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder resultDownload = new StringBuilder();
            URL url = null;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                if (url != null) {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader reader = new InputStreamReader(in);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String line = bufferedReader.readLine();  //читаем = pa


                    while (line != null) {
                        resultDownload.append(line + "\n");
                        //Log.i("!@#", line);

                        line = bufferedReader.readLine();
                    }

                    return resultDownload.toString();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }
    }

}