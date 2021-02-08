package com.wzz.Util.CertificateUtil;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Objects;

public class PDFUtil {

    private Document document;
    private PdfWriter writer;

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setWriter(PdfWriter writer) {
        this.writer = writer;
    }

    /**
     * 开启创建PDF对象
     *
     * @param pafPath ： 生成pdf的磁盘路径
     * @return
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public PDFUtil openDocument(String pafPath) throws FileNotFoundException, DocumentException {
        Document document = new Document(PageSize.A4);
        writer = PdfWriter.getInstance(document, new FileOutputStream(pafPath));
        document.open();
        this.document = document;
        return this;
    }

    /**
     * 添加图片背景
     *
     * @param absoluteX ：左边距
     * @param absoluteY ：底边距
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws DocumentException
     */
    public PDFUtil addImage(String imagePath, float absoluteX, float absoluteY) throws MalformedURLException, IOException, DocumentException {
        Image tImgCover = Image.getInstance(imagePath);
        tImgCover.setAbsolutePosition(absoluteX, absoluteY);
        float heigth = tImgCover.getHeight();
        float width = tImgCover.getWidth();
        // int percent=getPercent(heigth, width);
        int percent = getPercent2(heigth, width);
        // 设置图片居中显示
        // tImgCover.setAlignment(Image.MIDDLE);
        tImgCover.scalePercent(percent);// 表示是原来图像的比例;
        document.add(tImgCover);
        return this;
    }

    public PDFUtil addLogo(String imagePath, float absoluteX, float absoluteY) throws MalformedURLException, IOException, DocumentException {
        Image tImgCover = Image.getInstance(imagePath);
        tImgCover.setAbsolutePosition(absoluteX, absoluteY);
        tImgCover.scalePercent(20);// 表示是原来图像的比例;
        document.add(tImgCover);
        return this;
    }

    /**
     * @param certificateContent :pdf证书的中文内容
     * @param x                  ：左边距
     * @param y                  ：底边距
     * @param contentStyle       ：中文内容的样式
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public PDFUtil addContent(String certificateContent, float x, float y, ContentStyle contentStyle) throws DocumentException, IOException {

        if (contentStyle == null) {
            contentStyle = new ContentStyle();
        }

        PdfContentByte canvas = writer.getDirectContent();
        BaseFont bf = BaseFont.createFont(contentStyle.getTTFPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font secFont = new Font(bf, contentStyle.getFontSize(), contentStyle.getStyle(), contentStyle.getBaseColor());
        Phrase certificateContentPhrase = new Phrase(certificateContent, secFont);
        ColumnText.showTextAligned(canvas, contentStyle.getAlignment(), certificateContentPhrase, x, y, 0);
        return this;
    }

    /**
     * 添加日期内容
     *
     * @param x            插入pdf左边距
     * @param y            插入pdf底边距
     * @param contentStyle
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public PDFUtil addDateContent(float x, float y, ContentStyle contentStyle) throws DocumentException, IOException {

        if (contentStyle == null) {
            contentStyle = new ContentStyle();
        }

        Date currentDate = DateTimeUtil.getCurrentDate();
        String currentDateString = DateTimeUtil.DateToString(currentDate);

        PdfContentByte canvas = writer.getDirectContent();
        BaseFont bf = BaseFont.createFont(contentStyle.getTTFPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font secFont = new Font(bf, contentStyle.getFontSize(), contentStyle.getStyle(), contentStyle.getBaseColor());
        Phrase certificateDatephrase = new Phrase(currentDateString, secFont);
        ColumnText.showTextAligned(canvas, contentStyle.getAlignment(), certificateDatephrase, x, y, 0);
        return this;
    }

    /**
     * 释放资源
     */
    public void close() {
        document.close();
    }

    /**
     * 第二种解决方案，统一按照宽度压缩
     * 这样来的效果是，所有图片的宽度是相等的，自我认为给客户的效果是最好的
     */
    public int getPercent2(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        p2 = 595 / w * 100;
        System.out.println("--" + p2);
        p = Math.round(p2);
        return p;
    }

    /**
     * 第一种解决方案
     * 在不改变图片形状的同时，判断，如果h>w，则按h压缩，否则在w>h或w=h的情况下，按宽度压缩
     *
     * @param h
     * @param w
     * @return
     */

    public int getPercent(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        if (h > w) {
            p2 = 297 / h * 100;
        } else {
            p2 = 210 / w * 100;
        }
        p = Math.round(p2);
        return p;
    }

    public static void main(String[] args) throws IOException, DocumentException {
        long currentDateTime = new Date().getTime();
        String backgroundImage = Objects.requireNonNull(PDFUtil.class.getClassLoader().getResource("1.png")).getPath();
        String logo = Objects.requireNonNull(PDFUtil.class.getClassLoader().getResource("logo.png")).getPath();
        String pdfFilePath = "d:/" + 7 + ".pdf";
        PDFUtil pdfUtil = new PDFUtil();


        ContentStyle style1 = new ContentStyle();
        style1.setFontSize(15);

        ContentStyle style2 = new ContentStyle();
        style2.setFontSize(10);
        pdfUtil.openDocument(pdfFilePath)
                .addImage(backgroundImage, 0, 400)
                .addLogo(logo, 270, 460)
                .addContent("哈哈哈哈哈哈哈哈或或或或同学：", 85, 630, style1)
                .addContent("于" + DateTimeUtil.DateToString(new Date()) +
                        "在xxx测评中取得优异成绩!", 125, 590, style1)
                .addContent("特发此证,以资鼓励!", 125, 530, style2)
                .addContent("Power By WangZhouzhou", 360, 475, style2)
                .close();
    }
}
