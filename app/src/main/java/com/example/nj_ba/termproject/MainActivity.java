package com.example.nj_ba.termproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private Button btnAdd, btnDel, btnGps, btnEM, btn112;
    private TextView addTxt1, addTxt2;
    private ListView listView, listView2;
    String data;
    protected boolean bService = true;
    ArrayAdapter<String> adapter, adapter2;
    DbHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;
    protected CommStateListener commStateListener;
    protected TelephonyManager telephonyManager;
    protected MyLocationListener myLocationListener;
    protected LocationManager locationManager;
    protected double latitude, longitude, altitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateService();
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        commStateListener = new CommStateListener(telephonyManager, this);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnDel = (Button) findViewById(R.id.btnDel);
        btnGps = (Button) findViewById(R.id.btnGps);
        btnEM = (Button) findViewById(R.id.btnEM);
        btn112 = (Button) findViewById(R.id.btn112);
        listView = (ListView) findViewById(R.id.listView);
        listView2 = (ListView) findViewById(R.id.listView2);
        addTxt1 = (TextView) findViewById(R.id.addTxt1);
        addTxt2 = (TextView) findViewById(R.id.addTxt2);

        DBstart();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myLocationListener = new MyLocationListener();
        long minTime = 1000; // in ms
        float minDistance = 0;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, myLocationListener);
        btnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocation();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data = listView.getItemAtPosition(position).toString();
                final String data2 = listView2.getItemAtPosition(position).toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(data);
                builder.setMessage(data2);
                builder.setPositiveButton("전화",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + data2));
                                startActivity(dial);
                            }
                        });
                builder.setNegativeButton("문자",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent sms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + data2));
                                getIntent().putExtra("sms_body", "");
                                sms.putExtra("sms_body", "도와주세요");
                                startActivity(sms);
                            }
                        });
                builder.show();
            }
        });
        btnEM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&query=%EA%B8%B4%EA%B8%89+%EC%A0%84%ED%99%94%EB%B2%88%ED%98%B8&oquery=112&tqi=Utf3mdpVuEhsscmLlMCssssssA8-398712"));
                startActivity(intent);
            }
        });
        btn112.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:112"));
                startActivity(intent);
            }
        });
    }


    class DbHelper extends SQLiteOpenHelper {
        static final String DATABASE_NAME = "test.db";
        static final int DATABASE_VERSION = 1;

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE tableName (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, info TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS tableName");
            onCreate(db);
        }
    }

    void DBadd() {
        String name = addTxt1.getText().toString();
        String info = addTxt2.getText().toString();
        if (name.equals("") || info.equals("")) {
            Toast.makeText(getApplicationContext(), "정보를 입력해 주세요", Toast.LENGTH_SHORT).show();
            return;
        } else {
            db.execSQL("INSERT INTO tableName VALUES (null, '" + name + "', '" + info + "');");
            Toast.makeText(getApplicationContext(), "추가 성공", Toast.LENGTH_SHORT).show();

            addTxt1.setText(""); //입력시 EditText에 입력된값 지움
            addTxt2.setText("");
            cursor = db.rawQuery("SELECT * FROM tableName", null);
            startManagingCursor(cursor);    //엑티비티의 생명주기와 커서의 생명주기를 같게 한다.

            adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
            adapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);

            while (cursor.moveToNext()) {
                adapter.add(cursor.getString(1));
                adapter2.add(cursor.getString(2));
            }


            listView.setAdapter(adapter);
            listView2.setAdapter(adapter2);

        }
    }

    void DBDel() {
        String name = addTxt1.getText().toString();
        if (name.equals("")) {
            Toast.makeText(getApplicationContext(), "정보를 입력해 주세요", Toast.LENGTH_SHORT).show();
            return;
        } else {
            db.execSQL("DELETE FROM tableName WHERE name = '" + name + "';");
            Toast.makeText(getApplicationContext(), "삭제 성공", Toast.LENGTH_SHORT).show();
            cursor = db.rawQuery("SELECT * FROM tableName", null);
            startManagingCursor(cursor);    //엑티비티의 생명주기와 커서의 생명주기를 같게 한다.


            // ListView의 데이타를 저장할 Adapter 생성
            adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
            adapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);

            while (cursor.moveToNext()) {
                adapter.add(cursor.getString(1));
                adapter2.add(cursor.getString(2));
            }
            addTxt1.setText("");//입력시 EditText에 입력된값 지움
            addTxt2.setText("");

            listView.setAdapter(adapter);
            listView2.setAdapter(adapter2);
        }
    }


    void DBstart() {
        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();    // 읽기,쓰기 모드로 데이터베이스를 오픈

        cursor = db.rawQuery("SELECT * FROM tableName", null);
        startManagingCursor(cursor);    //엑티비티의 생명주기와 커서의 생명주기를 같게 한다.

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);

        while (cursor.moveToNext()) {
            adapter.add(cursor.getString(1));
            adapter2.add(cursor.getString(2));
        }

        //listView2.setVisibility(View.INVISIBLE);
        listView.setAdapter(adapter);
        listView2.setAdapter(adapter2);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBadd();

            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBDel();

            }
        });
    }
    private void updateService() {
        Intent intent = new Intent(this, PhoneCallService.class);
            startService(intent);
            bService = true;
    }
    private void showLocation() {

        latitude = myLocationListener.latitude;
        longitude = myLocationListener.longitude;
        altitude = myLocationListener.altitude;
        Toast.makeText(this, "Latitude: " + latitude + ", Longitude = " + longitude + ", Altitude  " + altitude, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + latitude + "," + longitude + "?z=15"));    //"geo:s36.321609,127.337957?z=20"
        startActivity(intent);
    }
    @Override
    protected void onResume() {//Onstart 다음에 실행 되는것(켜질때)
        super.onResume();
        telephonyManager.listen(commStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);//LISTEN_SIGNAL_STRENGTHS : 핸드폰에 있는 안테나 개수
        //메세지가 날아올때 처리를 callback 함수를 써줘야함
    }

    @Override
    protected void onPause() {//사용자에게 보여지다가 background로 들어간 상태(꺼질때)
        telephonyManager.listen(commStateListener, PhoneStateListener.LISTEN_NONE);//LISTEN_NONE을 이용해 종료
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        if (bService) {
            Intent intent = new Intent(this, PhoneCallService.class);
            stopService(intent);
        }
        super.onDestroy();
    }
}

