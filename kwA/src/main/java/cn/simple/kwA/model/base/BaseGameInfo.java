package cn.simple.kwA.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseGameInfo<M extends BaseGameInfo<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setType(java.lang.String type) {
		set("type", type);
	}

	public java.lang.String getType() {
		return getStr("type");
	}

	public void setUserId(java.lang.Integer userId) {
		set("user_id", userId);
	}

	public java.lang.Integer getUserId() {
		return getInt("user_id");
	}

	public void setNickName(java.lang.String nickName) {
		set("nick_name", nickName);
	}

	public java.lang.String getNickName() {
		return getStr("nick_name");
	}

	public void setIconUrl(java.lang.String iconUrl) {
		set("icon_url", iconUrl);
	}

	public java.lang.String getIconUrl() {
		return getStr("icon_url");
	}

	public void setTitile(java.lang.String titile) {
		set("titile", titile);
	}

	public java.lang.String getTitile() {
		return getStr("titile");
	}

	public void setPhaseNo(java.lang.String phaseNo) {
		set("phase_no", phaseNo);
	}

	public java.lang.String getPhaseNo() {
		return getStr("phase_no");
	}

	public void setStartTime(java.lang.String startTime) {
		set("start_time", startTime);
	}

	public java.lang.String getStartTime() {
		return getStr("start_time");
	}

	public void setEndTime(java.lang.String endTime) {
		set("end_time", endTime);
	}

	public java.lang.String getEndTime() {
		return getStr("end_time");
	}

	public void setEntryFee(java.math.BigDecimal entryFee) {
		set("entry_fee", entryFee);
	}

	public java.math.BigDecimal getEntryFee() {
		return get("entry_fee");
	}

	public void setReachStep(java.lang.Integer reachStep) {
		set("reach_step", reachStep);
	}

	public java.lang.Integer getReachStep() {
		return getInt("reach_step");
	}

	public void setEntryAmount(java.math.BigDecimal entryAmount) {
		set("entry_amount", entryAmount);
	}

	public java.math.BigDecimal getEntryAmount() {
		return get("entry_amount");
	}

	public void setEntryCount(java.lang.Integer entryCount) {
		set("entry_count", entryCount);
	}

	public java.lang.Integer getEntryCount() {
		return getInt("entry_count");
	}

	public void setReachCount(java.lang.Integer reachCount) {
		set("reach_count", reachCount);
	}

	public java.lang.Integer getReachCount() {
		return getInt("reach_count");
	}

	public void setAvgAmount(java.math.BigDecimal avgAmount) {
		set("avg_amount", avgAmount);
	}

	public java.math.BigDecimal getAvgAmount() {
		return get("avg_amount");
	}

	public void setStatus(java.lang.String status) {
		set("status", status);
	}

	public java.lang.String getStatus() {
		return getStr("status");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	public void setUpdateTime(java.util.Date updateTime) {
		set("update_time", updateTime);
	}

	public java.util.Date getUpdateTime() {
		return get("update_time");
	}

	public void setDelFlg(java.lang.String delFlg) {
		set("del_flg", delFlg);
	}

	public java.lang.String getDelFlg() {
		return getStr("del_flg");
	}

}
