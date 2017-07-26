package com.example.yejichoi.resttest1;

import java.util.List;

/**
 * Created by Yeji Choi on 2017-07-21.
 */

public class MemberBean extends CommonBean {

    private MemberBeanSub memberBean;
    private List<MemberBeanSub> memberList;

    class MemberBeanSub extends CommonBean {

        private String userId;
        private String userPw;
        private String name;
        private String addr;
        private String hp;
        private String profileImg;


        public String getProfileImg() {
            return profileImg;
        }

        public void setProfileImg(String profileImg) {
            this.profileImg = profileImg;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserPw() {
            return userPw;
        }

        public void setUserPw(String userPw) {
            this.userPw = userPw;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getHp() {
            return hp;
        }

        public void setHp(String hp) {
            this.hp = hp;
        }
    }

    public MemberBeanSub getMemberBean() {
        return memberBean;
    }

    public void setMemberBean(MemberBeanSub memberBean) {
        this.memberBean = memberBean;
    }

    public List<MemberBeanSub> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<MemberBeanSub> memberList) {
        this.memberList = memberList;
    }
}
