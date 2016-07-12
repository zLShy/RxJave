package zl.com.rxjave;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import cz.msebera.android.httpclient.Header;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends Activity {

    private Button btn;
    Context context;
    public static final String HOST = "http://www.weather.com.cn/data/sk/"; // 这是天气网站
    public static String[] CityCode = {"101270101", "101270102", "101270103", "101270104", "101270105", "101270106", "101270107", "101270108", "101270109", "101270110", "101270111", "101270112",
            "101270113", "101270114", "101270115", "101270116","101270201","101270202","101270203","204"};

    public static String weatherUrl = "http://api.yytianqi.com/weatherhours?city=CH270101&key=qa8g37q96gpb86e2";

//    http://www.weather.com.cn/data/sk/101270101.html
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        this.btn = (Button) findViewById(R.id.btn);

        this.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("TGA",doNetTask(weatherUrl));
                getCityWeather();
                Observable.just(new Stu("xiaoMing")).map(new Func1<Stu, String>() {

                    @Override
                    public String call(Stu stu) {
                        stu.setName(stu.getName() + "   \nNO:");
                        return stu.getName();
                    }
                }).map(new Func1<String, Stu>() {
                    @Override
                    public Stu call(String s) {
                        Stu stu = new Stu(s + "10");
                        return stu;
                    }
                }).subscribe(new Action1<Stu>() {
                    @Override
                    public void call(Stu stu) {
                        TextView tv = (TextView) findViewById(R.id.tv);
                        tv.setText(stu.getName());
                    }
                });
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.tv);
                        tv.setText("he he");
                    }
                });
            }
        });
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                subscriber.onNext("Hello RxJava!");
                subscriber.onCompleted();
            }
        });


        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Toast.makeText(getApplicationContext(), "yes", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        };

        observable.subscribe(subscriber);
    }


    private void getWeatherInfo() {

        Observable.just(CityCode).flatMap(new Func1<String[], Observable<?>>() {
            @Override
            public Observable<?> call(String[] strings) {

                return Observable.from(strings);
            }
        }).cast(String.class).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return doNetTask(HOST+s+".html");
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e("TGA", s);
            }
        });
    }

    private String doNetTask(String s) {

        HttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(s);
        String result;
        try {
            HttpResponse response = client.execute(get);

            if (200 == response.getStatusLine().getStatusCode()) {
                result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            } else {
                result = "状态行非200";
            }

        } catch (Exception e1) {
            result = e1.getStackTrace().toString();
        }
        return result;

    }

    private void getCityWeather() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(MainActivity.this, weatherUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String body = new String(responseBody);
                System.out.println(body);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
