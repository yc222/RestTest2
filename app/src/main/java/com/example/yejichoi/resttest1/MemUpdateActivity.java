package com.example.yejichoi.resttest1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MemUpdateActivity extends AppCompatActivity {

    private EditText mEdtSignId, mEdtSignPw, mEdtSignName,
            mEdtSignAddr, mEdtSignHp;

    private ImageView mImgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mem_update);

        MemberBean.MemberBeanSub memberBean = (MemberBean.MemberBeanSub) getIntent().getSerializableExtra("memberBean");
//        List<MemberBean.MemberBeanSub> list = (List<MemberBean.MemberBeanSub>)
//                getIntent().getSerializableExtra(Constants.INTENT_KEY_MEMBER_LIST);

        mEdtSignId = (EditText) findViewById(R.id.edtSignId);
        mEdtSignPw = (EditText) findViewById(R.id.edtSignPw);
        mEdtSignName = (EditText) findViewById(R.id.edtSignName);
        mEdtSignAddr = (EditText) findViewById(R.id.edtSignAddr);
        mEdtSignHp = (EditText) findViewById(R.id.edtSignHp);

        mImgProfile = (ImageView) findViewById(R.id.imgProfile);

        // 회원 정보 수정 버튼 이벤트 등록
        findViewById(R.id.btnUpdate).setOnClickListener(btnUpdateClick);

        mEdtSignId.setText(memberBean.getUserId());
        mEdtSignPw.setText(memberBean.getUserPw());
        mEdtSignName.setText(memberBean.getName());
        mEdtSignAddr.setText(memberBean.getAddr());
        mEdtSignHp.setText(memberBean.getHp());

        new ImageLoaderTask(mImgProfile).execute(Constants.BASE_URL + memberBean.getProfileImg());

    } // end onCreate

    // 회원 정보 수정 버튼 처리
    private View.OnClickListener btnUpdateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new UpdateProcTask().execute();
        }
    };

    class UpdateProcTask extends AsyncTask<String, Void, String> {

        // ipconfig 를 CMD에 치면 나오는 IP주소 사용
        public static final String URL_UPDATE_PROC
                = Constants.BASE_URL + "/rest/updateProc.do";
        public String userId, userPw, name, addr, hp;

        @Override
        protected void onPreExecute() {
            // 프로세스 다이얼로그 표시
            //mProgressBar.setVisibility(View.VISIBLE);

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

                // 업데이트할 항목들 ..
                // 변수를 doIn에서 바로
                // 사용 못하기 때문에
                // 위에 변수 선언따로 해주고 preExecute에서 값을 미리 준다
                MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
                map.add("userId", userId);
                map.add("userPw", userPw);
                map.add("name", name);
                map.add("addr", addr);
                map.add("hp", hp);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.ALL.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String,Object>> request2 = new HttpEntity<>(map, headers);

                return restTemplate2.postForObject(URL_UPDATE_PROC, request2, String.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //mProgressBar.setVisibility(View.INVISIBLE);

            Gson gson = new Gson();

            try{
                MemberBean bean =  gson.fromJson(s,MemberBean.class);
                if(bean != null) {
                    if(bean.getResult().equals("ok")) {
                        Toast.makeText(MemUpdateActivity.this, bean.getResultMsg(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Toast.makeText(MemUpdateActivity.this, bean.getResultMsg(), Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {
                Toast.makeText(MemUpdateActivity.this, "파싱 실패", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    } // end UpdateProcTask

    // 이미지 비동기 로딩
    class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView dispImageView;

        public ImageLoaderTask(ImageView dispImgView) {
            this.dispImageView = dispImgView;
        } // end ImageLoaderTask 생성자

        @Override
        protected Bitmap doInBackground(String... params) {
            String imgUrl = params[0];

            Bitmap bmp = null;

            try {
                bmp = BitmapFactory.decodeStream((InputStream) new URL(imgUrl).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bmp;
        } // end doInBackground()

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                //표시
                dispImageView.setImageBitmap(bitmap);
            }
        } // end onPostExecute

    } // end AsyncTask
}
