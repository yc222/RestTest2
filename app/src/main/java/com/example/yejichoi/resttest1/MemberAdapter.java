package com.example.yejichoi.resttest1;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yeji Choi on 2017-07-25.
 */

public class MemberAdapter extends BaseAdapter {

    private Context mContext;
    private List<MemberBean.MemberBeanSub> memberList = new ArrayList<MemberBean.MemberBeanSub>();

    // 생성자
    public MemberAdapter(Context context) {
        mContext = context;
        updateMemberTask();
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 이거 추가!!
    public void updateMemberTask() {
        new MemberTask().execute();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // 1. ListView에 표시하고자 하는 layout을 inflate한다.
        LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 레이아웃 가져오고  convertview에 정보가 담겨있음..?
        convertView = li.inflate(R.layout.lay_member, null);

        // 2. convertView에 뿌려줄 데이터 가져온다.
        final MemberBean.MemberBeanSub member = memberList.get(position);

        // 3. 인플레이트 한 레이아웃에서 찾는다, 컴포넌트들을.
        ImageView imgProfile = (ImageView)convertView.findViewById(R.id.imgProfile);
        TextView txtName = (TextView)convertView.findViewById(R.id.txtName);
        TextView txtId = (TextView)convertView.findViewById(R.id.txtId);

        // 데이터를 가져 온다.
        final MemberBean.MemberBeanSub bean = memberList.get(position);

        // 4. 찾은 컴포넌트에게 데이터 삽입
        new ImageLoaderTask(imgProfile).execute(Constants.BASE_URL + bean.getProfileImg());
        txtName.setText(bean.getName());
        txtId.setText(bean.getUserId());

        // 리스트뷰 클릭 이벤트 등록
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent i = new Intent(지금 나의 위치, 이동할 곳의 위치 )
                // 오류났던 부분이 MemUpdate에서 memberBean에 nullpointException들어와서
                // 그런건데, i.putExtra("bean", bean); 를 업데이트액티비티 의 memberBean으로 바꿈
                // 왜그런지 몰라 putExtra를 봐야할 듯
                // 빨간 줄 사라지게 ㅎㅏ려고 databean에 final(메모리 변경 안되게),
                // 하고 빈클래스에 serializable implement
                if( bean.getUserId() != null) {
                    Intent i = new Intent(mContext, MemUpdateActivity.class);
                    i.putExtra("memberBean", bean);
                    mContext.startActivity(i);

                }
                else {
                    Toast.makeText(mContext, bean.getResultMsg(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        return convertView;

    } // end getView

    class MemberTask extends AsyncTask<String, Void, String> {

        private String URL_MEMBER_LIST = Constants.BASE_URL + "/rest/selectMemberList.do";


        @Override
        protected String doInBackground(String... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

                MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.ALL.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String,Object>> request = new HttpEntity<>(map, headers);

                return restTemplate.postForObject(URL_MEMBER_LIST, request, String.class);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        } // end doInBackground

        @Override
        protected void onPostExecute(String s) {
            Gson gson = new Gson();

            try{
                MemberBean bean =  gson.fromJson(s,MemberBean.class);
                if(bean != null) {
                    if(bean.getResult().equals("ok")) {
                        memberList = bean.getMemberList();
                        // 회원 정보 수정 되면, 리스트 뷰를 갱신 시켜줌!->수정된 회원 데이터로 바로 바뀜
                        MemberAdapter.this.notifyDataSetInvalidated();
                    }
                }

            } catch (Exception e) {
                Toast.makeText(mContext, "파싱 실패", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        } // end onPostExecute

    } // end MemberTask


}

