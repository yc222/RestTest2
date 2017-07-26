package com.example.yejichoi.resttest1;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.List;

public class ListViewActivity extends AppCompatActivity {

    private ListView mListView;
    private MemberAdapter memberAdapter;
    private static int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        List<MemberBean.MemberBeanSub> list = (List<MemberBean.MemberBeanSub>)
                getIntent().getSerializableExtra(Constants.INTENT_KEY_MEMBER_LIST);

        mListView = (ListView) findViewById(R.id.listview);
        memberAdapter = new MemberAdapter(this);

        mListView.setAdapter(memberAdapter);

    } // end onCreate

    @Override
    protected void onResume() {
        super.onResume();
        // 데이터 갱신
        memberAdapter.updateMemberTask();
        // This is for scroll
        mListView.setSelection(pos);//스크롤 뷰 위치 설정

    } // end onResume

    @Override
    protected void onPause() {
        super.onPause();
        pos = mListView.getFirstVisiblePosition(); //현재 스크롤 뷰의 위치를 저장

    }
} // end class
