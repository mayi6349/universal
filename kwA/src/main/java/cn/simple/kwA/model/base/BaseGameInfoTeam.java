package cn.simple.kwA.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseGameInfoTeam<M extends BaseGameInfoTeam<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setUserId(java.lang.Integer userId) {
		set("user_id", userId);
	}

	public java.lang.Integer getUserId() {
		return getInt("user_id");
	}

	public void setTeamName(java.lang.String teamName) {
		set("team_name", teamName);
	}

	public java.lang.String getTeamName() {
		return getStr("team_name");
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

	public void setTeamCount(java.lang.Integer teamCount) {
		set("team_count", teamCount);
	}

	public java.lang.Integer getTeamCount() {
		return getInt("team_count");
	}

	public void setSumAmount(java.math.BigDecimal sumAmount) {
		set("sum_amount", sumAmount);
	}

	public java.math.BigDecimal getSumAmount() {
		return get("sum_amount");
	}

	public void setSumCount(java.lang.Integer sumCount) {
		set("sum_count", sumCount);
	}

	public java.lang.Integer getSumCount() {
		return getInt("sum_count");
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
