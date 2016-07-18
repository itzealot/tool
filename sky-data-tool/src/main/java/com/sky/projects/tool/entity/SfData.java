package com.sky.projects.tool.entity;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

import com.sky.projects.tool.util.IDCardUtil;

/**
 * 身份关系型数据实体，用于json序列化
 * 
 * @author zt
 *
 */
@JsonAutoDetect(JsonMethod.FIELD)
@SuppressWarnings("serial")
public class SfData implements Serializable {
	@JsonProperty("MAC")
	private String MAC = "";
	@JsonProperty("PHONE")
	private String PHONE = "";
	@JsonProperty("IMSI")
	private String IMSI = "";
	@JsonProperty("IMEI")
	private String IMEI = "";
	@JsonProperty("AUTH_TYPE")
	private String AUTH_TYPE = "";
	@JsonProperty("AUTH_CODE")
	private String AUTH_CODE = "";
	@JsonProperty("CERTIFICATE_TYPE")
	private String CERTIFICATE_TYPE = "";
	@JsonProperty("CERTIFICATE_CODE")
	private String CERTIFICATE_CODE = "";
	@JsonProperty("ID_TYPE")
	private String ID_TYPE = "";
	@JsonProperty("ACCOUNT")
	private String ACCOUNT = "";
	@JsonProperty("LAST_TIME")
	private int LAST_TIME = 0;
	@JsonProperty("LAST_PLACE")
	private String LAST_PLACE = "";

	public SfData() {
		super();
	}

	public SfData(String mAC, String pHONE, String iMSI, String iMEI, String aUTH_TYPE, String aUTH_CODE,
			String cERTIFICATE_TYPE, String cERTIFICATE_CODE, String iD_TYPE, String aCCOUNT, String lAST_PLACE) {
		super();
		this.MAC = mAC;
		this.PHONE = pHONE;
		this.IMSI = iMSI;
		this.IMEI = iMEI;
		this.AUTH_TYPE = aUTH_TYPE;
		this.AUTH_CODE = aUTH_CODE;
		this.CERTIFICATE_TYPE = cERTIFICATE_TYPE;
		this.CERTIFICATE_CODE = cERTIFICATE_CODE;
		this.ID_TYPE = iD_TYPE;
		this.ACCOUNT = aCCOUNT;
		this.LAST_PLACE = lAST_PLACE;
	}

	public SfData(String mAC, String pHONE, String iMSI, String iMEI, String aUTH_TYPE, String aUTH_CODE,
			String cERTIFICATE_TYPE, String cERTIFICATE_CODE, String iD_TYPE, String aCCOUNT, int lAST_TIME,
			String lAST_PLACE) {
		this(mAC, pHONE, iMSI, iMEI, aUTH_TYPE, aUTH_CODE, cERTIFICATE_TYPE, cERTIFICATE_CODE, iD_TYPE, aCCOUNT,
				lAST_PLACE);
		LAST_TIME = lAST_TIME;
	}

	public String getMAC() {
		return MAC;
	}

	public void setMAC(String mAC) {
		MAC = mAC;
	}

	public String getPHONE() {
		return PHONE;
	}

	public void setPHONE(String pHONE) {
		PHONE = pHONE;
	}

	public String getIMSI() {
		return IMSI;
	}

	public void setIMSI(String iMSI) {
		IMSI = iMSI;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public String getAUTH_TYPE() {
		return AUTH_TYPE;
	}

	public void setAUTH_TYPE(String aUTH_TYPE) {
		AUTH_TYPE = aUTH_TYPE;
	}

	public String getAUTH_CODE() {
		return AUTH_CODE;
	}

	public void setAUTH_CODE(String aUTH_CODE) {
		AUTH_CODE = aUTH_CODE;
	}

	public String getCERTIFICATE_TYPE() {
		return CERTIFICATE_TYPE;
	}

	public void setCERTIFICATE_TYPE(String cERTIFICATE_TYPE) {
		CERTIFICATE_TYPE = cERTIFICATE_TYPE;
	}

	public String getCERTIFICATE_CODE() {
		return CERTIFICATE_CODE;
	}

	public void setCERTIFICATE_CODE(String cERTIFICATE_CODE) {
		CERTIFICATE_CODE = cERTIFICATE_CODE;
	}

	public String getID_TYPE() {
		return ID_TYPE;
	}

	public void setID_TYPE(String iD_TYPE) {
		ID_TYPE = iD_TYPE;
	}

	public String getACCOUNT() {
		return ACCOUNT;
	}

	public void setACCOUNT(String aCCOUNT) {
		ACCOUNT = aCCOUNT;
	}

	public int getLAST_TIME() {
		return LAST_TIME;
	}

	public void setLAST_TIME(int lAST_TIME) {
		LAST_TIME = lAST_TIME;
	}

	public String getLAST_PLACE() {
		return LAST_PLACE;
	}

	public void setLAST_PLACE(String lAST_PLACE) {
		LAST_PLACE = lAST_PLACE;
	}

	@Override
	public String toString() {
		return "SfData [MAC=" + MAC + ", PHONE=" + PHONE + ", IMSI=" + IMSI + ", IMEI=" + IMEI + ", AUTH_TYPE="
				+ AUTH_TYPE + ", AUTH_CODE=" + AUTH_CODE + ", CERTIFICATE_TYPE=" + CERTIFICATE_TYPE
				+ ", CERTIFICATE_CODE=" + CERTIFICATE_CODE + ", ID_TYPE=" + ID_TYPE + ", ACCOUNT=" + ACCOUNT
				+ ", LAST_TIME=" + LAST_TIME + ", LAST_PLACE=" + LAST_PLACE + "]";
	}

	/**
	 * 根据原有错误数据，调整为新的数据
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public SfData map(BlockingQueue<String> datas, String line) throws Exception {
		// 标记是否被修改
		boolean flag = false;

		try {
			// 处理认证类型
			switch (this.AUTH_TYPE) {
			case "1020007":// 认证类型为身份证
				this.AUTH_TYPE = "";
				this.AUTH_CODE = IDCardUtil.transferIDCard(this.AUTH_CODE);

				flag = true;
				break;

			case "1020009":// 认证类型为 IMSI
				this.AUTH_TYPE = "";
				flag = true;
				break;

			// 固网ad认证账号
			case "1020001":
				flag = true;
				break;

			case "1020004":
			case "1020003":
				// 处理认证类型为手机,认证数据为手机号，长度为11
				if (this.AUTH_CODE.length() == 11 && isPhone(this.AUTH_CODE)) {
					this.AUTH_TYPE = "";
					flag = true;
				} else {// 认证数据为 IMSI
					this.AUTH_TYPE = "";
					flag = true;
				}

				break;

			// 其他类型的，识别身份证号和IMSI
			case "1029999":
				try {
					this.AUTH_CODE = IDCardUtil.transferIDCard(this.AUTH_CODE);
					this.AUTH_TYPE = "";
					flag = true;
				} catch (Exception e) {
					// 说明不是省份证
				}

				// IMSI
				if (this.AUTH_CODE.length() == 15 && this.AUTH_CODE.startsWith("460")) {
					this.AUTH_TYPE = "";
					flag = true;
				}

				break;

			// WLAN 处理手机
			case "1020005":
				if (isPhone(this.AUTH_CODE)) {
					this.AUTH_TYPE = "";
				}

				flag = true;
				break;

			// WLAN 处理手机
			case "1020008":
				if (isPhone(this.AUTH_CODE)) {
					this.AUTH_TYPE = "";
				}
				flag = true;
				break;
			}

			// 处理虚拟类型
			switch (this.ID_TYPE) {
			// QQ
			case "9941001":
			case "1421004":
			case "1090001":
			case "1060001":
			case "1030001":
				this.ID_TYPE = "";
				flag = true;
				break;

			// 微信
			case "9941003":
			case "1421005":
			case "1030036":
			case "1069982":// web 微信
				this.ID_TYPE = "";
				flag = true;
				break;

			// 新浪微博
			case "9940084":
			case "1424084":
			case "1330001":
			case "1259994":
				this.ID_TYPE = "1330001";
				flag = true;

				break;

			// 腾讯微博
			case "1330002":
			case "1259993":
				this.ID_TYPE = "1330002";
				flag = true;

				break;

			// 滴滴打车
			case "9940096":
			case "1424096":
			case "5070004":// 滴滴打车司机版
				this.ID_TYPE = "1279738";
				flag = true;

				break;

			// 淘宝旺旺
			case "1030022":
				this.ID_TYPE = "1030022";
				flag = true;

				break;

			// 京东商城
			case "9940044":
			case "1424044":
			case "1261016":
			case "1231019":
				this.ID_TYPE = "1279570";
				flag = true;

				break;

			// 支付宝
			case "1290002":
				this.ID_TYPE = "1290001";
				flag = true;

				break;

			// 58同城
			case "9940005":
			case "9940004":
			case "5030003":
			case "1424004":
			case "1301013":
			case "1271017":
			case "1199964":
			case "1079964":
				this.ID_TYPE = "1279594";
				flag = true;

				break;

			// 美团
			case "1425041":
			case "1221022":
				this.ID_TYPE = "1279669";
				flag = true;

				break;

			// 12306
			case "9859992":
			case "1271125":
			case "1271352":// 12306 订票助手
				this.ID_TYPE = "1279584";
				flag = true;

				break;
			}

			// 处理证件类型
			switch (this.CERTIFICATE_TYPE) {
			// 身份证
			case "111":
				this.CERTIFICATE_TYPE = "";
				this.CERTIFICATE_CODE = IDCardUtil.transferIDCard(this.CERTIFICATE_CODE);
				flag = true;
				break;
			}
		} catch (Exception e) {
			throw new Exception("transfer data error.", e);
		}

		if (!flag) {
			datas.put(line);
		}

		return flag ? this : null;
	}

	/**
	 * 根据原有错误数据，调整为新的数据
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public SfData map(String line) throws Exception {
		// 标记是否被修改
		boolean flag = false;

		try {
			// 处理认证类型
			switch (this.AUTH_TYPE) {
			case "1020007":// 认证类型为身份证
				this.AUTH_TYPE = "";
				this.AUTH_CODE = IDCardUtil.transferIDCard(this.AUTH_CODE);

				flag = true;
				break;

			case "1020009":// 认证类型为 IMSI
				this.AUTH_TYPE = "";
				flag = true;
				break;

			// 固网ad认证账号
			case "1020001":
				flag = true;
				break;

			case "1020004":
			case "1020003":
				// 处理认证类型为手机,认证数据为手机号，长度为11
				if (this.AUTH_CODE.length() == 11 && isPhone(this.AUTH_CODE)) {
					this.AUTH_TYPE = "";
					flag = true;
				} else {// 认证数据为 IMSI
					this.AUTH_TYPE = "";
					flag = true;
				}

				break;

			// 其他类型的，识别身份证号和IMSI
			case "1029999":
				try {
					this.AUTH_CODE = IDCardUtil.transferIDCard(this.AUTH_CODE);
					this.AUTH_TYPE = "";
					flag = true;
				} catch (Exception e) {
					// 说明不是省份证
				}

				// IMSI
				if (this.AUTH_CODE.length() == 15 && this.AUTH_CODE.startsWith("460")) {
					this.AUTH_TYPE = "";
					flag = true;
				}

				break;

			// WLAN 处理手机
			case "1020005":
				if (isPhone(this.AUTH_CODE)) {
					this.AUTH_TYPE = "";
				}

				flag = true;
				break;

			// WLAN 处理手机
			case "1020008":
				if (isPhone(this.AUTH_CODE)) {
					this.AUTH_TYPE = "";
				}
				flag = true;
				break;
			}

			// 处理虚拟类型
			switch (this.ID_TYPE) {
			// QQ
			case "9941001":
			case "1421004":
			case "1090001":
			case "1060001":
			case "1030001":
				this.ID_TYPE = "";
				flag = true;
				break;

			// 微信
			case "9941003":
			case "1421005":
			case "1030036":
			case "1069982":// web 微信
				this.ID_TYPE = "";
				flag = true;
				break;

			// 新浪微博
			case "9940084":
			case "1424084":
			case "1330001":
			case "1259994":
				this.ID_TYPE = "1330001";
				flag = true;

				break;

			// 腾讯微博
			case "1330002":
			case "1259993":
				this.ID_TYPE = "1330002";
				flag = true;

				break;

			// 滴滴打车
			case "9940096":
			case "1424096":
			case "5070004":// 滴滴打车司机版
				this.ID_TYPE = "1279738";
				flag = true;

				break;

			// 淘宝旺旺
			case "1030022":
				this.ID_TYPE = "1030022";
				flag = true;

				break;

			// 京东商城
			case "9940044":
			case "1424044":
			case "1261016":
			case "1231019":
				this.ID_TYPE = "1279570";
				flag = true;

				break;

			// 支付宝
			case "1290002":
				this.ID_TYPE = "1290001";
				flag = true;

				break;

			// 58同城
			case "9940005":
			case "9940004":
			case "5030003":
			case "1424004":
			case "1301013":
			case "1271017":
			case "1199964":
			case "1079964":
				this.ID_TYPE = "1279594";
				flag = true;

				break;

			// 美团
			case "1425041":
			case "1221022":
				this.ID_TYPE = "1279669";
				flag = true;

				break;

			// 12306
			case "9859992":
			case "1271125":
			case "1271352":// 12306 订票助手
				this.ID_TYPE = "1279584";
				flag = true;

				break;
			}
		} catch (Exception e) {
			throw new Exception("transfer data error.", e);
		}

		return flag ? this : null;
	}

	private boolean isPhone(String phone) {
		String regexPhone = "^((\\+?86)|(\\(\\+86\\))|852)?(13[0-9][0-9]{8}|15[0-9][0-9]{8}|18[0-9][0-9]{8}|14[0-9][0-9]{8}|17[0-9][0-9]{8}|[0-9]{8})$";

		Pattern pattern = Pattern.compile(regexPhone);

		return pattern.matcher(phone).matches();
	}

}
