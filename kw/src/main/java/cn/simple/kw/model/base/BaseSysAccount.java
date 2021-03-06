package cn.simple.kw.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSysAccount<M extends BaseSysAccount<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setBalance(java.math.BigDecimal balance) {
		set("balance", balance);
	}
	
	public java.math.BigDecimal getBalance() {
		return get("balance");
	}

	public void setUsableBalance(java.math.BigDecimal usableBalance) {
		set("usable_balance", usableBalance);
	}
	
	public java.math.BigDecimal getUsableBalance() {
		return get("usable_balance");
	}

	public void setFrozenBalance(java.math.BigDecimal frozenBalance) {
		set("frozen_balance", frozenBalance);
	}
	
	public java.math.BigDecimal getFrozenBalance() {
		return get("frozen_balance");
	}

	public void setSumPayCount(java.lang.Integer sumPayCount) {
		set("sum_pay_count", sumPayCount);
	}
	
	public java.lang.Integer getSumPayCount() {
		return getInt("sum_pay_count");
	}

	public void setSumPayAmount(java.math.BigDecimal sumPayAmount) {
		set("sum_pay_amount", sumPayAmount);
	}
	
	public java.math.BigDecimal getSumPayAmount() {
		return get("sum_pay_amount");
	}

	public void setSumIncomeCount(java.lang.Integer sumIncomeCount) {
		set("sum_income_count", sumIncomeCount);
	}
	
	public java.lang.Integer getSumIncomeCount() {
		return getInt("sum_income_count");
	}

	public void setSumIncomeAmount(java.math.BigDecimal sumIncomeAmount) {
		set("sum_income_amount", sumIncomeAmount);
	}
	
	public java.math.BigDecimal getSumIncomeAmount() {
		return get("sum_income_amount");
	}

	public void setSumWithdrawCount(java.lang.Integer sumWithdrawCount) {
		set("sum_withdraw_count", sumWithdrawCount);
	}
	
	public java.lang.Integer getSumWithdrawCount() {
		return getInt("sum_withdraw_count");
	}

	public void setSumWithdrawAmount(java.math.BigDecimal sumWithdrawAmount) {
		set("sum_withdraw_amount", sumWithdrawAmount);
	}
	
	public java.math.BigDecimal getSumWithdrawAmount() {
		return get("sum_withdraw_amount");
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

}
