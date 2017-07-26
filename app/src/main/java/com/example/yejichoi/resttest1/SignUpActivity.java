package com.example.yejichoi.resttest1;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEdtSignId, mEdtSignPw, mEdtSignName,
            mEdtSignAddr, mEdtSignHp;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEdtSignId = (EditText)findViewById(R.id.edtSignId);
        mEdtSignPw = (EditText)findViewById(R.id.edtSignPw);
        mEdtSignName = (EditText)findViewById(R.id.edtSignName);
        mEdtSignAddr = (EditText)findViewById(R.id.edtSignAddr);
        mEdtSignHp = (EditText)findViewById(R.id.edtSignHp);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        findViewById(R.id.btnSignUp).setOnClickListener(btnSignUpClick);

    } // end OnCreate

    // 회원 가입 버튼 처리
    private View.OnClickListener btnSignUpClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            new SignUpProcTask().execute();

        }
    };

    private class SignUpProcTask extends AsyncTask<String, Void, String> {

        // ipconfig 를 CMD에 치면 나오는 IP주소 사용
        public static final String URL_SIGNUP_PROC
                = "http://172.16.8.161:8888/rest/signUpProc.do";
        public String userId, userPw, name, addr, hp;

        @Override
        protected void onPreExecute() {
            // 프로세스 다이얼로그 표시
            mProgressBar.setVisibility(View.VISIBLE);

            userId = mEdtSignId.getText().toString();
            userPw = mEdtSignPw.getText().toString();
            name = mEdtSignName.getText().toString();
            addr = mEdtSignAddr.getText().toString();
            hp = mEdtSignHp.getText().toString();

        } // end onPreExecute

        @Override
        protected String doInBackground(String... params) {
            try {
                RestTemplate restTemplate2 = new RestTemplate();
                //restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate2.getMessageConverters().add(new FormHttpMessageConverter());

                MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
                map.add("userId", userId);
                map.add("userPw", userPw);
                map.add("name", name);
                map.add("addr", addr);
                map.add("hp", hp);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.ALL.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String,Object>> request2 = new HttpEntity<>(map, headers);

                return restTemplate2.postForObject(URL_SIGNUP_PROC, request2, String.class);

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
                        // 회원가입 성공
                        Toast.makeText(SignUpActivity.this, bean.getResult(),
                                Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SignUpActivity.this, ListViewActivity.class);
                        startActivity(i);

                    } else {
                        // 회원가입 실패
                        Toast.makeText(SignUpActivity.this, bean.getResultMsg(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {
                Toast.makeText(SignUpActivity.this, "파싱 실패", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        } // end onPostExecute

    } // end AsyncTask

} // end class
