package org.zarroboogs.weibo.greendao.bean;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.google.gson.Gson;

import org.zarroboogs.weibo.bean.MessageBean;
// KEEP INCLUDES END
/**
 * Entity mapped to table "time_line_status_table".
 */
public class Green_TimeLineStatus implements java.io.Serializable {

    private Integer _id;
    private String accountid;
    private String mblogid;
    private String json;

    // KEEP FIELDS - put your custom fields here
    private MessageBean mMessageBean;
    // KEEP FIELDS END

    public Green_TimeLineStatus() {
    }

    public Green_TimeLineStatus(Integer _id, String accountid, String mblogid, String json) {
        this._id = _id;
        this.accountid = accountid;
        this.mblogid = mblogid;
        this.json = json;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public String getMblogid() {
        return mblogid;
    }

    public void setMblogid(String mblogid) {
        this.mblogid = mblogid;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    // KEEP METHODS - put your custom methods here

    public MessageBean getMessageBean() {
        if (mMessageBean == null){
            mMessageBean = new Gson().fromJson(getJson(), MessageBean.class);
        }
        return mMessageBean;
    }
    // KEEP METHODS END

}
