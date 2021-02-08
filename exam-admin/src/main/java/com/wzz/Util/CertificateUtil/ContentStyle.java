package com.wzz.Util.CertificateUtil;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;

public class ContentStyle {

	private String TTFPath = "C:/WINDOWS/Fonts/SIMYOU.TTF";// 字体类型
	private float  fontSize = 12;//字体大小
	private BaseColor baseColor = new BaseColor(0, 0, 0);//默认是黑色
	private int style = Font.NORMAL;//字体样式
	private int alignment = Element.ALIGN_LEFT;

	public String getTTFPath() {
		return TTFPath;
	}
	public void setTTFPath(String tTFPath) {
		TTFPath = tTFPath;
	}
	public float getFontSize() {
		return fontSize;
	}
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}
	public BaseColor getBaseColor() {
		return baseColor;
	}
	public void setBaseColor(BaseColor baseColor) {
		this.baseColor = baseColor;
	}
	public int getStyle() {
		return style;
	}
	public void setStyle(int style) {
		this.style = style;
	}
	public int getAlignment() {
		return alignment;
	}
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
}
