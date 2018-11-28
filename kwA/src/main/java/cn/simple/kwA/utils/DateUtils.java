package cn.simple.kwA.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 时间工具
 * 
 * @author malongbo
 */
public final class DateUtils {

	/**
	 * Number of milliseconds in a standard second.
	 * 
	 * @since 2.1
	 */
	public static final long MILLIS_PER_SECOND = 1000;
	/**
	 * Number of milliseconds in a standard minute.
	 * 
	 * @since 2.1
	 */
	public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
	/**
	 * Number of milliseconds in a standard hour.
	 * 
	 * @since 2.1
	 */
	public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
	/**
	 * Number of milliseconds in a standard day.
	 * 
	 * @since 2.1
	 */
	public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

	public static void main(String[] args) throws ParseException {
		String sDateStr = "20181101121411";
		System.out.println(getDateBegin());
		// System.out.println(getTime("20180831101047"));
		// System.out.println(getDateTime("20180831101047"));
		// System.out.println(getTomorrowYYYYMMDD());
		// System.out.println(getDateTime(getEndDayOfTomorrow()));
	}

	/**
	 * 获取指定时间对应的延时
	 * 
	 * @param time
	 *            "HH:mm:ss"
	 */
	public static long getInitDelay(String time) {
		long initDelay;
		initDelay = getTimeMillis(time) - System.currentTimeMillis();
		initDelay = initDelay > 0 ? initDelay : MILLIS_PER_DAY + initDelay;

		return initDelay;
	}

	/**
	 * 获取指定时间对应的毫秒数
	 * 
	 * @param time
	 *            "HH:mm:ss"
	 */
	public static long getTimeMillis(String time) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
			Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
			return curDate.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获得当前时间 格式为：yyyy-MM-dd HH:mm:ss
	 */
	public static String getNowTime() {
		Date nowday = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒
		String time = sdf.format(nowday);
		return time;
	}

	/**
	 * 获得当前时间 格式为：yyyy-MM-dd HH:mm:ss
	 */
	public static String getDateTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒
		String time = sdf.format(date);
		return time;
	}

	/**
	 * 获得当前时间 格式为：yyyy-MM-dd HH:mm:ss
	 */
	public static String getNowTimeYMDHMSS() {
		Date nowday = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 精确到秒
		String time = sdf.format(nowday);
		return time;
	}

	/**
	 * 获取当前系统时间戳
	 * 
	 * @return
	 */
	public static Long getNowTimeStamp() {
		return System.currentTimeMillis();
	}

	public static Long getNowDateTime() {
		return new Date().getTime() / 1000;
		// return new Date().getTime()/1000;
	}

	public static Long getDateBegin() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 001);
		return cal.getTimeInMillis() / 1000;
	}

	public static Long getDateBegin(Long time) throws ParseException {
		return getTime(getYMD(getTime(time)) + " 00:00:00");
	}

	/**
	 * 自定义日期格式
	 * 
	 * @param format
	 * @return
	 */
	public static String getNowTime(String format) {
		Date nowday = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);// 精确到秒
		String time = sdf.format(nowday);
		return time;
	}

	/**
	 * 自定义日期格式
	 * 
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static String getFormatTime(String data, String format) throws ParseException {
		Date nowday = getDateTime(data);
		SimpleDateFormat sdf = new SimpleDateFormat(format);// 精确到秒
		String time = sdf.format(nowday);
		return time;
	}

	/**
	 * 自定义日期格式
	 * 
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static String getYMD(String timeStr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 精确到秒
		String time = sdf.format(getDateTime(timeStr));
		return time;
	}

	/**
	 * 自定义日期格式
	 * 
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static String getYMD() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 精确到秒
		String time = sdf.format(new Date());
		return time;
	}

	/**
	 * 将时间字符转成Unix时间戳
	 * 
	 * @param timeStr
	 * @return
	 * @throws java.text.ParseException
	 */
	public static Long getTime(String timeStr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒
		Date date = sdf.parse(timeStr);
		return date.getTime() / 1000;
	}

	public static Long getBeginTime(String timeStr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// 精确到秒
		Date date = sdf.parse(timeStr);
		return date.getTime() / 1000;
	}

	/**
	 * String转Date
	 * 
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateTime(String strDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(strDate);
	}

	/**
	 * 将时间字符转成Unix时间戳
	 * 
	 * @param timeStr
	 * @return
	 * @throws java.text.ParseException
	 */
	public static Long getTimeMin(String timeStr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 精确到秒
		Date date = sdf.parse(timeStr);
		return date.getTime() / 1000;
	}

	/**
	 * 将Unix时间戳转成时间字符
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String getTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒
		Date date = new Date(timestamp * 1000);
		return sdf.format(date);
	}

	/**
	 * 获取半年后的时间 时间字符格式为：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return 时间字符串
	 */
	public static String getHalfYearLaterTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒

		Calendar calendar = Calendar.getInstance();
		int currMonth = calendar.get(Calendar.MONTH) + 1;

		if (currMonth >= 1 && currMonth <= 6) {
			calendar.add(Calendar.MONTH, 6);
		} else {
			calendar.add(Calendar.YEAR, 1);
			calendar.set(Calendar.MONTH, currMonth - 6 - 1);
		}

		return sdf.format(calendar.getTime());
	}

	/**
	 * 获取下一天的时间 时间字符格式为：yyyyMMdd
	 * 
	 * @return 时间字符串
	 */
	public static String getNextDateTime(String today) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// 年月日

		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(today));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		return sdf.format(calendar.getTime());
	}

	/** 默认的格式化方式 */
	private static final String defaultFormat = "yyyy-MM-dd HH:mm:ss";

	public static String getDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentDate = new Date();
		String formatCurrentDate = dateFormat.format(currentDate).toString();

		return formatCurrentDate;
	}

	public static String getCurrentDate() {
		String format = "yyyyMMdd";
		Date date = new Date();
		date.setTime(System.currentTimeMillis());
		if (format == null || "".equals(format.trim())) {
			format = defaultFormat;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static String getCurrentTime() {
		String format = "yyyyMMddHHmmss";
		Date date = new Date();
		date.setTime(System.currentTimeMillis());
		if (format == null || "".equals(format.trim())) {
			format = defaultFormat;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	// 获取当天的开始时间
	public static String getTodayYYYYMMDD() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// 年月日
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return sdf.format(cal.getTime());
	}

	// 获取昨天的开始时间
	public static String getYesterdayYYYYMMDD() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// 年月日
		Calendar cal = new GregorianCalendar();
		cal.setTime(getDayBegin());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return sdf.format(cal.getTime());
	}

	// 获取明天的开始时间
	public static String getTomorrowYYYYMMDD() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// 年月日
		Calendar cal = new GregorianCalendar();
		cal.setTime(getDayBegin());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return sdf.format(cal.getTime());
	}

	// 获取当天的开始时间
	public static java.util.Date getDayBegin() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	// 获取当天的结束时间
	public static java.util.Date getDayEnd() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	// 获取昨天的开始时间
	public static Date getBeginDayOfYesterday() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getDayBegin());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	// 获取昨天的结束时间
	public static Date getEndDayOfYesterDay() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getDayEnd());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	// 获取明天的开始时间
	public static Date getBeginDayOfTomorrow() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getDayBegin());
		cal.add(Calendar.DAY_OF_MONTH, 1);

		return cal.getTime();
	}

	// 获取明天的结束时间
	public static Date getEndDayOfTomorrow() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getDayEnd());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	// 获取本周的开始时间
	public static Date getBeginDayOfWeek() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayofweek == 1) {
			dayofweek += 7;
		}
		cal.add(Calendar.DATE, 2 - dayofweek);
		return getDayStartTime(cal.getTime());
	}

	// 获取本周的结束时间
	public static Date getEndDayOfWeek() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getBeginDayOfWeek());
		cal.add(Calendar.DAY_OF_WEEK, 6);
		Date weekEndSta = cal.getTime();
		return getDayEndTime(weekEndSta);
	}

	// 获取本月的开始时间
	public static Date getBeginDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(getNowYear(), getNowMonth() - 1, 1);
		return getDayStartTime(calendar.getTime());
	}

	// 获取本月的结束时间
	public static Date getEndDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(getNowYear(), getNowMonth() - 1, 1);
		int day = calendar.getActualMaximum(5);
		calendar.set(getNowYear(), getNowMonth() - 1, day);
		return getDayEndTime(calendar.getTime());
	}

	// 获取本年的开始时间
	public static java.util.Date getBeginDayOfYear() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, getNowYear());
		// cal.set
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DATE, 1);

		return getDayStartTime(cal.getTime());
	}

	// 获取本年的结束时间
	public static java.util.Date getEndDayOfYear() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, getNowYear());
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DATE, 31);
		return getDayEndTime(cal.getTime());
	}

	// 获取某个日期的开始时间
	public static Timestamp getDayStartTime(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d)
			calendar.setTime(d);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Timestamp(calendar.getTimeInMillis());
	}

	// 获取某个日期的结束时间
	public static Timestamp getDayEndTime(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d)
			calendar.setTime(d);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return new Timestamp(calendar.getTimeInMillis());
	}

	// 获取今年是哪一年
	public static Integer getNowYear() {
		Date date = new Date();
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		return Integer.valueOf(gc.get(1));
	}

	// 获取本月是哪一月
	public static int getNowMonth() {
		Date date = new Date();
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		return gc.get(2) + 1;
	}

	// 两个日期相减得到的天数
	public static int getDiffDays(Date beginDate, Date endDate) {

		if (beginDate == null || endDate == null) {
			throw new IllegalArgumentException("getDiffDays param is null!");
		}

		long diff = (endDate.getTime() - beginDate.getTime()) / (1000 * 60 * 60 * 24);

		int days = new Long(diff).intValue();

		return days;
	}

	// 两个日期相减得到的毫秒数
	public static long dateDiff(Date beginDate, Date endDate) {
		long date1ms = beginDate.getTime();
		long date2ms = endDate.getTime();
		return date2ms - date1ms;
	}

	// 获取两个日期中的最大日期
	public static Date max(Date beginDate, Date endDate) {
		if (beginDate == null) {
			return endDate;
		}
		if (endDate == null) {
			return beginDate;
		}
		if (beginDate.after(endDate)) {
			return beginDate;
		}
		return endDate;
	}

	// 获取两个日期中的最小日期
	public static Date min(Date beginDate, Date endDate) {
		if (beginDate == null) {
			return endDate;
		}
		if (endDate == null) {
			return beginDate;
		}
		if (beginDate.after(endDate)) {
			return endDate;
		}
		return beginDate;
	}

	// 返回某月该季度的第一个月
	public static Date getFirstSeasonDate(Date date) {
		final int[] SEASON = { 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4 };
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int sean = SEASON[cal.get(Calendar.MONTH)];
		cal.set(Calendar.MONTH, sean * 3 - 3);
		return cal.getTime();
	}

	// 返回某个日期下几天的日期
	public static Date getNextDay(Date date, int i) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) + i);
		return cal.getTime();
	}

	// 返回某个日期前几天的日期
	public static Date getFrontDay(Date date, int i) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - i);
		return cal.getTime();
	}

	/**
	 * yyyyMMddHHmmss 转 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param data
	 * @return
	 * @throws ParseException
	 */
	public static String formatData(String data) throws ParseException {
		String reg = "(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})";
		return data.replaceAll(reg, "$1-$2-$3 $4:$5:$6");
	}
}
