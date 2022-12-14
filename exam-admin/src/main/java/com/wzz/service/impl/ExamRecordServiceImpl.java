package com.wzz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzz.annotation.Cache;
import com.wzz.entity.Answer;
import com.wzz.entity.ExamQuestion;
import com.wzz.entity.ExamRecord;
import com.wzz.entity.User;
import com.wzz.exception.BusinessException;
import com.wzz.exception.CommonErrorCode;
import com.wzz.mapper.AnswerMapper;
import com.wzz.mapper.ExamQuestionMapper;
import com.wzz.mapper.ExamRecordMapper;
import com.wzz.mapper.UserMapper;
import com.wzz.service.ExamRecordService;
import com.wzz.utils.CertificateUtil.ContentStyle;
import com.wzz.utils.CertificateUtil.DateTimeUtil;
import com.wzz.utils.CertificateUtil.PDFUtil;
import com.wzz.utils.JwtUtils;
import com.wzz.utils.RedisUtil;
import com.wzz.utils.SaltEncryption;
import com.wzz.vo.PageResponse;
import com.wzz.vo.TokenVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.wzz.utils.CommonUtils.setEqualsQueryWrapper;

/**
 * @author by wzz
 * @implNote 2020/10/20 9:05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    private final UserMapper userMapper;

    private final ExamRecordMapper examRecordMapper;

    private final ExamQuestionMapper examQuestionMapper;

    private final AnswerMapper answerMapper;

    private final RedisUtil redisUtil;

    @Override
    public PageResponse<ExamRecord> getUserGrade(String username, Integer examId, Integer pageNo, Integer pageSize) {
        User user = Optional.ofNullable(userMapper.selectOne(new QueryWrapper<User>().eq("username", username))).orElseThrow(() -> new BusinessException(CommonErrorCode.E_100102));

        QueryWrapper<ExamRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user.getId());
        setEqualsQueryWrapper(wrapper, Collections.singletonMap("exam_id", examId));

        IPage<ExamRecord> page = examRecordMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);

        return PageResponse.<ExamRecord>builder().data(page.getRecords()).total(page.getTotal()).build();
    }

    @Cache(prefix = "exam:record", suffix = "#recordId", ttl = 10, randomTime = 2, timeUnit = TimeUnit.HOURS)
    @Override
    public ExamRecord getExamRecordById(Integer recordId) {
        return examRecordMapper.selectById(recordId);
    }

    @Override
    public void createExamCertificate(HttpServletResponse response, String examName, Integer examRecordId) {
        //  1. ????????????????????????
        ExamRecord examRecord = getExamRecordById(examRecordId);
        //  2. ???????????????????????????????????????
        User user = Optional.ofNullable(userMapper.selectById(examRecord.getUserId())).orElse(User.builder().trueName("??????????????????").build());

        //  ****windows??????????????????****
        //  ??????????????????????????????
        String backgroundImage = Objects.requireNonNull(PDFUtil.class.getClassLoader().getResource("static/images/certificateBg.png")).getPath();
        //  ???????????????????????????Logo
        String logo = Objects.requireNonNull(PDFUtil.class.getClassLoader().getResource("static/images/logo.png")).getPath();
        //  ?????????pdf???????????????(????????????????????????)
        String pdfFilePath = Objects.requireNonNull(PDFUtil.class.getClassLoader().getResource("static/templateCertificate.pdf")).getPath();


        //  ****linux?????????????????????(????????????????????????????????????)
//         String backgroundImage = "/exam/images/certificateBg.png";
//         String logo = "/exam/images/logo.png";
//         String pdfFilePath = "/exam/templateCertificate.pdf";

        //  ???????????????
        PDFUtil pdfUtil = new PDFUtil();

        //  ??????????????????
        ContentStyle style1 = new ContentStyle();
        style1.setFontSize(15);
        ContentStyle style2 = new ContentStyle();
        style2.setFontSize(10);

        //  ??????????????????????????????
        String trueName = user.getTrueName();
        Date examTime = examRecord.getExamTime();
        //  ??????XXX????????????
        String userInfo = trueName + "?????????";
        //  ??????????????????
        String content = "??????" + DateTimeUtil.DateToString(examTime) + "???" + examName + "???????????????????????????!";
        //  ????????????
        try {
            pdfUtil.openDocument(pdfFilePath).addImage(backgroundImage, 0, 400).addLogo(logo, 270, 480).addContent(userInfo, 85, 630, style1).addContent("????????????,????????????!", 125, 495, style2).addContent("Power By WangZhouzhou", 360, 495, style2);
            //  ??????????????????????????????
            int end;
            //  ??????????????????,????????????????????????
            for (int i = 0, y = 590; i < content.length(); y -= 30) {
                end = Math.min(i + 30, content.length());
                pdfUtil.addContent(content.substring(i, end), 125, y, style1);
                i = end;
            }
        } catch (Exception e) {
            log.error("??????????????????: " + e);
        }
        //  ????????????pdf?????????
        pdfUtil.close();
        //  ????????????
        if (pdfFilePath.contains("%")) {
            try {
                pdfFilePath = URLDecoder.decode(pdfFilePath, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //  ?????????
        ServletOutputStream out = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(pdfFilePath);
            String[] dir = pdfFilePath.split("/");
            //  ???????????????
            String fileName = dir[dir.length - 1];
            String[] array = fileName.split("[.]");
            //  ????????????
            String fileType = array[array.length - 1].toLowerCase();
            // ????????????ContentType??????
            if ("jpg,jepg,gif,png".contains(fileType)) {// ????????????
                response.setContentType("image/" + fileType);
            } else if ("pdf".contains(fileType)) {// pdf??????
                response.setContentType("application/pdf");
            } else {// ??????????????????????????????
                response.setContentType("multipart/form-data");
            }
            // ????????????????????????????????????????????????????????????
            // response.setHeader("Content-Disposition", "attachment;fileName="+fileName);
            out = response.getOutputStream();
            //  ???????????????
            int len;
            byte[] buffer = new byte[1024 * 10];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            log.error("responseFileStream error:FileNotFoundException" + e);
        } catch (Exception e) {
            log.error("responseFileStream error:" + e);
        } finally {
            try {
                assert out != null;
                out.close();
                in.close();
            } catch (NullPointerException e) {
                log.error("responseFileStream stream close() error:NullPointerException" + e);
            } catch (Exception e) {
                log.error("responseFileStream stream close() error:" + e);
            }
        }
    }

    @Override
    public Integer addExamRecord(ExamRecord examRecord, HttpServletRequest request) {
        // ???????????????????????????
        TokenVo tokenVo = JwtUtils.getUserInfoByToken(request);
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", tokenVo.getUsername()));
        // ???????????????????????????
        examRecord.setUserId(user.getId());
        // ??????id
        List<ExamRecord> examRecords = examRecordMapper.selectList(null);
        int id = 1;
        if (examRecords.size() > 0) {
            id = examRecords.get(examRecords.size() - 1).getRecordId() + 1;
        }
        examRecord.setRecordId(id);

        // ???????????????????????????
        // ?????????????????????????????????
        List<Answer> answers = answerMapper.selectList(new QueryWrapper<Answer>().in("question_id", Arrays.asList(examRecord.getQuestionIds().split(","))));
        // ??????????????????????????????
        HashMap<String, String> map = new HashMap<>();// key????????????id  value???????????????
        ExamQuestion examQuestion = examQuestionMapper.selectOne(new QueryWrapper<ExamQuestion>().eq("exam_id", examRecord.getExamId()));
        // ?????????id
        String[] ids = examQuestion.getQuestionIds().split(",");
        // ?????????????????????????????????
        String[] scores = examQuestion.getScores().split(",");
        for (int i = 0; i < ids.length; i++) {
            map.put(ids[i], scores[i]);
        }
        // ????????????
        int logicScore = 0;
        // ?????????id
        StringBuilder sf = new StringBuilder();
        // ???????????????
        String[] userAnswers = examRecord.getUserAnswers().split("-");
        for (int i = 0; i < examRecord.getQuestionIds().split(",").length; i++) {
            int index = SaltEncryption.getIndex(answers, Integer.parseInt(examRecord.getQuestionIds().split(",")[i]));
            if (index != -1) {
                if (Objects.equals(userAnswers[i], answers.get(index).getTrueOption())) {
                    logicScore += Integer.parseInt(map.get(examRecord.getQuestionIds().split(",")[i]));
                } else {
                    sf.append(examRecord.getQuestionIds().split(",")[i]).append(",");
                }
            }
        }
        examRecord.setLogicScore(logicScore);
        if (sf.length() > 0) {// ?????????????????????
            examRecord.setErrorQuestionIds(sf.substring(0, sf.toString().length() - 1));
        }

        System.out.println(examRecord);
        examRecord.setExamTime(new Date());
        examRecordMapper.insert(examRecord);
        return id;
    }

    @Override
    public PageResponse<ExamRecord> getExamRecord(Integer examId, Integer pageNo, Integer pageSize) {
        QueryWrapper<ExamRecord> wrapper = new QueryWrapper<>();
        setEqualsQueryWrapper(wrapper, Collections.singletonMap("exam_id", examId));

        IPage<ExamRecord> page = examRecordMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);

        return PageResponse.<ExamRecord>builder().data(page.getRecords()).total(page.getTotal()).build();
    }

    @Override
    public void setObjectQuestionScore(Integer totalScore, Integer examRecordId) {
        ExamRecord examRecord = examRecordMapper.selectOne(new QueryWrapper<ExamRecord>().eq("record_id", examRecordId));
        examRecord.setTotalScore(totalScore);
        examRecordMapper.update(examRecord, new UpdateWrapper<ExamRecord>().eq("record_id", examRecordId));
        redisUtil.del("exam:record:" + examRecordId);
    }
}
