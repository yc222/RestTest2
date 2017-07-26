package com.example.yejichoi.resttest1;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class MainActivity extends AppCompatActivity {

    private EditText mEdtUserId, mEdtUserPw;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEdtUserId = (EditText) findViewById(R.id.edtUserId);
        mEdtUserPw = (EditText) findViewById(R.id.edtUserPw);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        findViewById(R.id.btnLogin).setOnClickListener(btnLoginClick);
        findViewById(R.id.btnSign).setOnClickListener(btnSignClick);


    }

    // 회원 가입 버튼 처리
    private View.OnClickListener btnSignClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();

        }
    };

    // 로그인 버튼 클릭 처리
    private View.OnClickListener btnLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            new LoginProcTask().execute();

        }
    };

    // 로그인 처리
    private class LoginProcTask extends AsyncTask<String, Void, String> {

        // ipconfig 를 CMD에 치면 나오는 IP주소 사용
        public static final String URL_LOGIN_PROC
                = Constants.BASE_URL + "/rest/loginProc.do";
        public String userId, userPw;

        @Override
        protected void onPreExecute() {
            // 프로세스 다이얼로그 표시
            mProgressBar.setVisibility(View.VISIBLE);

            userId = mEdtUserId.getText().toString();
            userPw = mEdtUserPw.getText().toString();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                RestTemplate restTemplate = new RestTemplate();
                //restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

                MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
                map.add("userId", userId);
                map.add("userPw", userPw);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.ALL.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String,Object>> request = new HttpEntity<>(map, headers);

                return restTemplate.postForObject(URL_LOGIN_PROC, request, String.class);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        } // end doInBackground

        @Override
        protected void onPostExecute(String s) {

            mProgressBar.setVisibility(View.INVISIBLE);

            Gson gson = new Gson();

            try{
                MemberBean bean =  gson.fromJson(s,MemberBean.class);
                if(bean != null) {
                    if(bean.getResult().equals("ok")) {
                        // 로그인 성공
                        //Intent i = new Intent(MainActivity.this, MemUpdateActivity.class);
                        Intent i = new Intent(MainActivity.this, ListViewActivity.class);
                        i.putExtra("memberBean", bean.getMemberBean());
                        startActivity(i);

                    } else {
                        // 로그인 실패
                        Toast.makeText(MainActivity.this, bean.getResultMsg(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "파싱 실패", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        } // end onPostExecute
    }; // end AsyncTask
}
