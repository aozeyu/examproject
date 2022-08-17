package com.example.examproject.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.examproject.Pojo.*;
import com.example.examproject.Service.impl.*;
import com.example.examproject.Utils.OSSUtil;
import com.example.examproject.Utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @program: examproject
 * @description: 22
 * @packagename: com.example.examproject.Controller
 * @author: 姚泽宇
 * @date: 2022-08-16 00:42
 **/

@RestController
@RequestMapping(value = "/teacher")
@Slf4j
@Api(tags = "老师权限相关的接口")
public class TeacherController {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private QuestionServiceImpl questionService;

    @Autowired
    private QuestionBankServiceImpl questionBankService;
    @Autowired
    private AnswerServiceImpl answerService;
    //注入自己的redis工具类
    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/getQuestionBank")
    @ApiOperation("获取所有题库信息")
    public CommonResult<Object> getQuestionBank() {
        log.info("执行了===>TeacherController中的getQuestionBank方法");
        if (redisUtil.get("questionBanks") != null){
            return new CommonResult<>(200, "success", redisUtil.get("questionBanks"));
        }else {
            List<QuestionBank> questionBanks = questionBankService.list(new QueryWrapper<>());
            //设置默认缓存时间(10分钟) + 随机缓存时间(0-5分钟 )  来防止缓存雪崩和击穿
            redisUtil.set("questionBanks", questionBanks, 60 * 10 + new Random().nextInt(5) * 60);
            return new CommonResult<>(200, "success", questionBanks);
        }
    }

    @GetMapping("/getQuestion")
    @ApiOperation("获取题目信息,可分页 ----> 查询条件(可无)(questionType,questionBank,questionContent),必须有的(pageNo,pageSize)")

    public CommonResult<List<Question>> getQuestion(@RequestParam(required = false) String questionType,@RequestParam(required = false) String questionBank, @RequestParam(required = false) String questionContent,Integer pageNo, Integer pageSize) {
        log.info("执行了===>TeacherController中的getQuestion方法");
        //参数一是当前页，参数二是每页个数
        IPage<Question> questionIPage = new Page<>(pageNo,pageSize);
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        if (!Objects.equals(questionType, "")) wrapper.eq("qu_type", questionType);
        if (!Objects.equals(questionBank, "")) wrapper.like("qu_bank_name", questionBank);
        if (!Objects.equals(questionContent, "")) wrapper.like("qu_content", questionContent);
        questionIPage = questionService.page(questionIPage,wrapper);
        List<Question> questions = questionIPage.getRecords();
        return new CommonResult<>(200,"success",questions);
    }

    @GetMapping("/deleteQuestion")
    @ApiOperation("根据id批量删除")
    public CommonResult<String> deleteQuestion(String questionIds) throws InterruptedException{
        log.info("执行了===>TeacherController中的deleteQuestion方法");
        String[] ids = questionIds.split(",");
        boolean flag = true;
        for (String id: ids){
            if (!questionService.removeById(Integer.parseInt(id))) {
                flag = false;
            }
        }
        return flag ? new CommonResult<>(200, "删除成功") : new CommonResult<>(233, "删除失败");
    }

    @GetMapping("/addBankQuestion")
    @ApiOperation("将问题加入题库")
    public CommonResult<String> addBankQuestion(String questionIds, String banks) {
        log.info("执行了===>TeacherController中的addBankQuestion方法");
        boolean flag = false;
        //需要操作的问题
        String[] quIds = questionIds.split(",");
        //需要放入的题库id
        String[] bankIds = banks.split(",");

        //将每一个题目放入每一个题库中
        for (String quId : quIds) {
            Question question = questionService.getById(Integer.parseInt(quId));
            String quBankId = question.getQuBankId();
            //当前已经有的题库id
            String[] qid = quBankId.split(",");
            System.out.println(quBankId);
            //存在去重后的题库id
            Set<Integer> allId = new HashSet<>();
            if (!quBankId.equals("")){
                for (String s : qid) {
                    allId.add(Integer.parseInt(s));
                }
            }
            //将新增的仓库id放入
            for (String bankId : bankIds) {
                allId.add(Integer.parseInt(bankId));
            }
            //处理后的id字符串 例如(1,2,3)
            String handleHaveBankIds = allId.toString().replaceAll(" ", "");
            handleHaveBankIds = handleHaveBankIds.substring(1, handleHaveBankIds.length() - 1);
            question.setQuBankId(handleHaveBankIds);
            StringBuilder bankNames = new StringBuilder();
            for (Integer id : allId) {
                bankNames.append(questionBankService.getById(id).getBankName()).append(",");
            }
            question.setQuBankName(bankNames.toString().substring(0,bankNames.toString().length() -1 ));
            flag = questionService.update(question,new UpdateWrapper<Question>().eq("id",question.getId()));

        }
        return flag ?new CommonResult<>(200, "添加题库成功") : new CommonResult<>(233, "添加题库失败");
    }
    @GetMapping("/removeBankQuestion")
    @ApiOperation("将问题从题库移除")
    public CommonResult<String> removeBankQuestion(String questionIds,String banks) {
        log.info("执行了===>TeacherController中的removeBankQuestion方法");
        boolean flag = false;
        //需要操作的问题
        String[] quIds = questionIds.split(",");
        //需要移除的题库id
        String[] bankIds = banks.split(",");
        //操作需要移除仓库的问题
        for (String quId : quIds) {
            Question question = questionService.getById(Integer.parseInt(quId));
            String quBankId = question.getQuBankId();
            //当前问题拥有的仓库id
            String[] curHaveId = quBankId.split(",");
            //存储处理后的id
            Set<Integer> handleId = new HashSet<>();

            if (!quBankId.equals("")){
                for (String s : curHaveId) {
                    handleId.add(Integer.parseInt(s));
                }
            }
            //遍历查询set中是否含有需要删除的仓库id

            for (String bankId : bankIds) {
                handleId.remove(Integer.parseInt(bankId));
            }
            //处理后的id字符串 例如(1,2,3)
            String handleHaveBankIds = handleId.toString().replaceAll(" ", "");
            handleHaveBankIds = handleHaveBankIds.substring(1, handleHaveBankIds.length() - 1);
            //更新当前用户的题库id值
            question.setQuBankId(handleHaveBankIds);

            if (!handleHaveBankIds.equals("")){
                //将存放处理后的set集合遍历,然后替换数据库的题库名
                StringBuilder bankNames = new StringBuilder();
                for (Integer id:handleId){
                    bankNames.append(questionBankService.getById(id).getBankName()).append(",");
                }
                //替换原来的仓库名称
                question.setQuBankName(bankNames.toString().substring(0, bankNames.toString().length() - 1));


            }else {
                question.setQuBankName("");
            }
            //更新问题对象
            flag = questionService.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
        }
        return flag ? new CommonResult<>(200, "移除题库成功") : new CommonResult<>(233, "移除题库失败");
    }

    @PostMapping("/uploadQuestionImage")
    @ApiOperation("接受前端上传的图片,返回上传图片地址")
    public CommonResult<String> uploadQuestionImage(MultipartFile file)throws Exception{
        log.info("执行了===>TeacherController中的uploadQuestionImage方法");
        System.out.println(file.getOriginalFilename());
        String url = OSSUtil.picOSS(file);
        return new CommonResult<>(200, "上传成功", url);
    }



    @PostMapping("/addQuestion")
    @ApiOperation("添加试题")
    public CommonResult<String> addQuestion(@RequestBody QuestionVo questionVo) {
        log.info("执行了===>TeacherController中的addQuestion方法");
        //查询所有的问题,然后就可以设置当前问题的id了
        List<Question> qus = questionService.list(new QueryWrapper<>());
        Integer currentQuId = qus.size() + 1;
        Question question = new Question();
        //设置基础字段
        question.setQuType(questionVo.getQuestionType());
        question.setId(currentQuId);
        question.setCreateTime(new Date());
        question.setLevel(questionVo.getQuestionLevel());
        question.setAnalysis(questionVo.getAnalysis());
        question.setQuContent(questionVo.getQuestionContent());
        question.setCreatePerson(questionVo.getCreatePerson());
        //设置所属题库
        String bankIds = Arrays.toString(questionVo.getBankId());
        question.setQuBankId(bankIds.substring(1, bankIds.length() - 1).replaceAll(" ", ""));
        //设置题目插图
        if (questionVo.getImages().length != 0) {
            String QuImages = Arrays.toString(questionVo.getImages());
            question.setImage(QuImages.substring(1, QuImages.length() - 1).replaceAll(" ", ""));
        }
        StringBuilder bankNames = new StringBuilder();
        for (Integer integer : questionVo.getBankId()) {
            bankNames.append(questionBankService.getById(integer).getBankName()).append(",");
        }
        String names = bankNames.toString();
        names = names.substring(0, names.length() - 1);
        question.setQuBankName(names);

        questionService.save(question);
        //设置答案对象
        StringBuffer multipleChoice = new StringBuffer();
        if (questionVo.getQuestionType() != 4) {//不为简答题
            Answer answer = new Answer();
            answer.setQuestionId(currentQuId);
            StringBuffer imgs = new StringBuffer();
            StringBuffer answers = new StringBuffer();
            for (int i = 0; i < questionVo.getAnswer().length; i++) {
                if (questionVo.getAnswer()[i].getImages().length > 0) {//如果该选项有一张图片信息
                    imgs.append(questionVo.getAnswer()[i].getImages()[0]).append(",");
                }
                answers.append(questionVo.getAnswer()[i].getAnswer()).append(",");
                //设置对的选项的下标值
                if (questionVo.getQuestionType() == 2) {//多选
                    if (questionVo.getAnswer()[i].getIsTrue().equals("true")) multipleChoice.append(i).append(",");
                } else {//单选和判断 都是仅有一个答案
                    if (questionVo.getAnswer()[i].getIsTrue().equals("true")) {
                        answer.setTrueOption(i + "");
                        answer.setAnalysis(questionVo.getAnswer()[i].getAnalysis());
                    }
                }
            }
            if (questionVo.getQuestionType() == 2)
                answer.setTrueOption(multipleChoice.toString().substring(0, multipleChoice.toString().length() - 1));
            String handleImgs = imgs.toString();
            String handleAnswers = answers.toString();
            if (handleImgs.length() != 0) handleImgs = handleImgs.substring(0, handleImgs.length() - 1);
            if (handleAnswers.length() != 0) handleAnswers = handleAnswers.substring(0, handleAnswers.length() - 1);

            //设置答案的图片
            answer.setImages(handleImgs);
            //设置所有的选项
            answer.setAllOption(handleAnswers);
            answerService.save(answer);
        }
        return new CommonResult<>(200, "新增题目成功");
    }
}
