package com.wzz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wzz.Util.OSSUtil;
import com.wzz.Util.RedisUtil;
import com.wzz.entity.Answer;
import com.wzz.entity.Question;
import com.wzz.entity.QuestionBank;
import com.wzz.entity.User;
import com.wzz.service.impl.*;
import com.wzz.vo.BankHaveQuestionSum;
import com.wzz.vo.CommonResult;
import com.wzz.vo.QuestionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2020/10/24 15:42
 * @created by wzz
 */
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

    //jackson
    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/getQuestionBank")
    @ApiOperation("获取所有题库信息")
    public CommonResult<Object> getQuestionBank() {
        log.info("执行了===>TeacherController中的getQuestionBank方法");
        if (redisUtil.get("questionBanks") != null) {//redis中有缓存

            return new CommonResult<>(200, "success", redisUtil.get("questionBanks"));
        } else {//redis无缓存
            List<QuestionBank> questionBanks = questionBankService.list(new QueryWrapper<>());
            //设置默认缓存时间(10分钟) + 随机缓存时间(0-5分钟 )  来防止缓存雪崩和击穿
            redisUtil.set("questionBanks", questionBanks, 60 * 10 + new Random().nextInt(5) * 60);
            return new CommonResult<>(200, "success", questionBanks);
        }
    }

    @GetMapping("/getQuestion")
    @ApiOperation("获取题目信息,可分页 ----> 查询条件(可无)(questionType,questionBank,questionContent),必须有的(pageNo,pageSize)")
    public CommonResult<List<Question>> getQuestion(@RequestParam(required = false) String questionType,
                                                    @RequestParam(required = false) String questionBank,
                                                    @RequestParam(required = false) String questionContent,
                                                    Integer pageNo, Integer pageSize) {
        log.info("执行了===>TeacherController中的getQuestion方法");
        //参数一是当前页，参数二是每页个数
        IPage<Question> questionPage = new Page<>(pageNo, pageSize);
        //查询条件(可选)
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        if (!Objects.equals(questionType, "")) wrapper.eq("qu_type", questionType);
        if (!Objects.equals(questionBank, "")) wrapper.like("qu_bank_name", questionBank);
        if (!Objects.equals(questionContent, "")) wrapper.like("qu_content", questionContent);

        questionPage = questionService.page(questionPage, wrapper);

        List<Question> questions = questionPage.getRecords();
        return new CommonResult<>(200, "success", questions);
    }

    @GetMapping("/deleteQuestion")
    @ApiOperation("根据id批量删除")
    public CommonResult<String> deleteQuestion(String questionIds) throws InterruptedException {
        log.info("执行了===>TeacherController中的deleteQuestion方法");
        String[] ids = questionIds.split(",");
        boolean flag = true;
        for (String id : ids) {
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
            //当前的问题对象
            Question question = questionService.getById(Integer.parseInt(quId));
            String quBankId = question.getQuBankId();
            //当前已经有的题库id
            String[] qid = quBankId.split(",");
            System.out.println(quBankId);
            //存在去重后的题库id
            Set<Integer> allId = new HashSet<>();
            if (!quBankId.equals("")) {//防止题目没有题库
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
            //更新当前用户的题库id值
            question.setQuBankId(handleHaveBankIds);

            //将存放处理后的set集合遍历,然后替换数据库的题库名
            StringBuilder bankNames = new StringBuilder();
            for (Integer id : allId) {
                bankNames.append(questionBankService.getById(id).getBankName()).append(",");
            }
            //替换原来的仓库名称
            question.setQuBankName(bankNames.toString().substring(0, bankNames.toString().length() - 1));
            //更新问题对象
            flag = questionService.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
        }
        return flag ? new CommonResult<>(200, "添加题库成功") : new CommonResult<>(233, "添加题库失败");
    }

    @GetMapping("/removeBankQuestion")
    @ApiOperation("将问题从题库移除")
    public CommonResult<String> removeBankQuestion(String questionIds, String banks) {
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
            if (!quBankId.equals("")) {
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

            if (!handleHaveBankIds.equals("")) {//删除后还存在剩余的题库
                //将存放处理后的set集合遍历,然后替换数据库的题库名
                StringBuilder bankNames = new StringBuilder();
                for (Integer id : handleId) {
                    bankNames.append(questionBankService.getById(id).getBankName()).append(",");
                }
                //替换原来的仓库名称
                question.setQuBankName(bankNames.toString().substring(0, bankNames.toString().length() - 1));
            } else {//不剩题库了
                question.setQuBankName("");
            }
            //更新问题对象
            flag = questionService.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
        }
        return flag ? new CommonResult<>(200, "移除题库成功") : new CommonResult<>(233, "移除题库失败");
    }

    @PostMapping("/uploadQuestionImage")
    @ApiOperation("接受前端上传的图片,返回上传图片地址")
    public CommonResult<String> uploadQuestionImage(MultipartFile file) throws Exception {
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
        Integer currentQuId = qus.get(qus.size() - 1).getId() + 1;
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

    @GetMapping("/getQuestionById/{id}")
    @ApiOperation("根据id获取题目信息")
    public CommonResult<Object> getQuestionById(@PathVariable("id") Integer id) {
        log.info("执行了===>TeacherController中的getQuestionById方法");
        if (redisUtil.get("questionVo:" + id) != null) {
            return new CommonResult<>(200, "查询题目信息成功", redisUtil.get("questionVo:" + id));
        } else {
            Question question = questionService.getById(id);
            Answer answer = answerService.getOne(new QueryWrapper<Answer>().eq("question_id", id));
            QuestionVo questionVo = new QuestionVo();
            //设置字段
            questionVo.setQuestionContent(question.getQuContent());
            questionVo.setAnalysis(question.getAnalysis());
            questionVo.setQuestionType(question.getQuType());
            questionVo.setQuestionLevel(question.getLevel());
            questionVo.setQuestionId(question.getId());
            if (question.getImage() != null && !Objects.equals(question.getImage(), ""))
                questionVo.setImages(question.getImage().split(","));
            questionVo.setCreatePerson(question.getCreatePerson());
            //设置所属题库
            if (!Objects.equals(question.getQuBankId(), "")) {
                String[] bids = question.getQuBankId().split(",");
                Integer[] bankIds = new Integer[bids.length];
                for (int i = 0; i < bids.length; i++) {
                    bankIds[i] = Integer.parseInt(bids[i]);
                }
                questionVo.setBankId(bankIds);
            }
            if (answer != null) {
                if (question.getQuType() != 2) {
                    String[] allOption = answer.getAllOption().split(",");
                    String[] imgs = answer.getImages().split(",");
                    QuestionVo.Answer[] qa = new QuestionVo.Answer[allOption.length];
                    for (int i = 0; i < allOption.length; i++) {
                        QuestionVo.Answer answer1 = new QuestionVo.Answer();
                        answer1.setId(i);
                        answer1.setAnswer(allOption[i]);
                        if (i <= imgs.length - 1 && !Objects.equals(imgs[i], ""))
                            answer1.setImages(new String[]{imgs[i]});
                        if (i == Integer.parseInt(answer.getTrueOption())) {
                            answer1.setIsTrue("true");
                            answer1.setAnalysis(answer.getAnalysis());
                        }
                        qa[i] = answer1;
                    }
                    questionVo.setAnswer(qa);
                } else {//多选
                    String[] allOption = answer.getAllOption().split(",");
                    String[] imgs = answer.getImages().split(",");
                    QuestionVo.Answer[] qa = new QuestionVo.Answer[allOption.length];
                    for (int i = 0; i < allOption.length; i++) {
                        QuestionVo.Answer answer1 = new QuestionVo.Answer();
                        answer1.setId(i);
                        answer1.setAnswer(allOption[i]);
                        answer1.setImages(imgs);
                        if (i == Integer.parseInt(answer.getTrueOption().split(",")[i])) {
                            answer1.setIsTrue("true");
                            answer1.setAnalysis(answer.getAnalysis());
                        }
                        qa[i] = answer1;
                    }
                    questionVo.setAnswer(qa);
                }
            }
            redisUtil.set("questionVo:" + id, questionVo, 60 * 5 * new Random().nextInt(2));
            return new CommonResult<>(200, "查询成功", questionVo);
        }
    }

    @PostMapping("/updateQuestion")
    @ApiOperation("更新试题")
    public CommonResult<String> updateQuestion(@RequestBody QuestionVo questionVo) {
        log.info("执行了===>TeacherController中的updateQuestion方法");
        Question question = new Question();
        //设置基础字段
        question.setQuType(questionVo.getQuestionType());
        question.setId(questionVo.getQuestionId());
        question.setCreateTime(new Date());
        question.setLevel(questionVo.getQuestionLevel());
        question.setAnalysis(questionVo.getAnalysis());
        question.setQuContent(questionVo.getQuestionContent());
        question.setCreatePerson(questionVo.getCreatePerson());
        //设置所属题库
        String bankIds = Arrays.toString(questionVo.getBankId());
        question.setQuBankId(bankIds.substring(1, bankIds.length() - 1).replaceAll(" ", ""));
        //设置题目插图
        if (questionVo.getImages() != null && questionVo.getImages().length != 0) {
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
        //更新
        questionService.update(question, new UpdateWrapper<Question>().eq("id", questionVo.getQuestionId()));
        //设置答案对象
        StringBuffer multipleChoice = new StringBuffer();
        if (questionVo.getQuestionType() != 4) {//不为简答题
            Answer answer = new Answer();
            answer.setQuestionId(questionVo.getQuestionId());
            StringBuffer imgs = new StringBuffer();
            StringBuffer answers = new StringBuffer();
            for (int i = 0; i < questionVo.getAnswer().length; i++) {
                if (questionVo.getAnswer()[i].getImages() != null && questionVo.getAnswer()[i].getImages().length > 0) {//如果该选项有一张图片信息
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
            answerService.update(answer, new UpdateWrapper<Answer>().eq("question_id", questionVo.getQuestionId()));
        }
        return new CommonResult<>(200, "更新题目成功");
    }

    @GetMapping("/getBankHaveQuestionSumByType")
    @ApiOperation("获取题库中所有题目类型的数量")
    public CommonResult<Object> getBankHaveQuestionSumByType(@RequestParam(required = false) String bankName, Integer pageNo, Integer pageSize) {
        log.info("执行了===>TeacherController中的getBankHaveQuestionSumByType方法");

        //参数一是当前页，参数二是每页个数
        IPage<QuestionBank> questionBankIPage = new Page<>(pageNo, pageSize);
        //查询条件(可选)
        QueryWrapper<QuestionBank> wrapper = new QueryWrapper<>();
        if (!Objects.equals(bankName, "") && bankName != null) wrapper.like("bank_name", bankName);
        IPage<QuestionBank> iPage = questionBankService.page(questionBankIPage, wrapper);
        List<QuestionBank> questionBanks = iPage.getRecords();

        //封装成传给前端的数据类型
        List<BankHaveQuestionSum> bankHaveQuestionSums = new ArrayList<>();
        for (QuestionBank questionBank : questionBanks) {
            //创建vo对象
            BankHaveQuestionSum bankHaveQuestionSum = new BankHaveQuestionSum();
            //设置属性
            bankHaveQuestionSum.setQuestionBank(questionBank);
            //设置单选题的数量
            List<Question> singleQuestions = questionService.list(new QueryWrapper<Question>().eq("qu_type", 1).like("qu_bank_name", questionBank.getBankName()));
            bankHaveQuestionSum.setSingleChoice(singleQuestions.size());
            //设置多选题的数量
            List<Question> multipleQuestions = questionService.list(new QueryWrapper<Question>().eq("qu_type", 2).like("qu_bank_name", questionBank.getBankName()));
            bankHaveQuestionSum.setMultipleChoice(multipleQuestions.size());
            //设置判断题的数量
            List<Question> judgeQuestions = questionService.list(new QueryWrapper<Question>().eq("qu_type", 3).like("qu_bank_name", questionBank.getBankName()));
            bankHaveQuestionSum.setJudge(judgeQuestions.size());
            //设置简答题的数量
            List<Question> shortAnswerQuestions = questionService.list(new QueryWrapper<Question>().eq("qu_type", 4).like("qu_bank_name", questionBank.getBankName()));
            bankHaveQuestionSum.setShortAnswer(shortAnswerQuestions.size());
            //加入list中
            bankHaveQuestionSums.add(bankHaveQuestionSum);
        }
        return new CommonResult<>(200, "查询题库和所属题目信息成功", bankHaveQuestionSums);
    }

    @GetMapping("/deleteQuestionBank")
    @ApiOperation("删除题库并去除所有题目中的包含此题库的信息")
    public CommonResult<String> deleteQuestionBank(String ids) {
        String[] bankId = ids.split(",");
        for (String s : bankId) {
            //找到题库
            QuestionBank questionBank = questionBankService.getById(s);
            //找到与此题库相关的所有问题信息
            List<Question> questions = questionService.list(new QueryWrapper<Question>().like("qu_bank_name", questionBank.getBankName()));
            //移除与此题库相关的信息
            for (Question question : questions) {
                String quBankName = question.getQuBankName();
                String quBankId = question.getQuBankId();
                String[] name = quBankName.split(",");
                String[] id = quBankId.split(",");
                //新的题库名
                String[] newName = new String[name.length - 1];
                //新的题库id数据
                String[] newId = new String[id.length - 1];

                for (int i = 0, j = 0; i < name.length; i++) {
                    if (!name[i].equals(questionBank.getBankName())) {
                        newName[j] = name[i];
                        j++;
                    }
                }
                for (int i = 0, j = 0; i < id.length; i++) {
                    if (!id[i].equals(String.valueOf(questionBank.getBankId()))) {
                        newId[j] = id[i];
                        j++;
                    }
                }
                String handleName = Arrays.toString(newName)
                        .replaceAll(" ", "")
                        .replaceAll("]", "")
                        .replace("[", "");
                String handleId = Arrays.toString(newId).replaceAll(" ", "")
                        .replaceAll("]", "")
                        .replace("[", "");
                //设置删除题库后的新字段
                question.setQuBankName(handleName);
                question.setQuBankId(handleId);
                //更新题目
                questionService.update(question, new UpdateWrapper<Question>().eq("id", question.getId()));
            }
            //删除题库
            questionBankService.removeById(Integer.parseInt(s));
        }
        return new CommonResult<>(200, "删除题库成功");
    }

    @PostMapping("/addQuestionBank")
    @ApiOperation("添加题库信息")
    public CommonResult<String> addQuestionBank(@RequestBody QuestionBank questionBank) {
        boolean flag = questionBankService.save(questionBank);
        return flag ? new CommonResult<>(200, "添加题库成功") : new CommonResult<>(200, "添加题库失败");
    }

    @GetMapping("/getBankById")
    @ApiOperation("通过题库id获取题库信息")
    public CommonResult<QuestionBank> getBankById(Integer id) {
        return new CommonResult<>(200, "查询题库信息成功", questionBankService.getById(id));
    }

    @GetMapping("/getQuestionByBank")
    @ApiOperation("根据题库获取所有的题目信息(单选,多选,判断题)")
    public CommonResult<Object> getQuestionByBank(Integer bankId) {
        if (redisUtil.get("questionBankQuestion:" + bankId) != null) {//查询缓存
            return new CommonResult<>(200, "当前题库题目查询成功", redisUtil.get("questionBankQuestion:" + bankId));
        } else {
            QuestionBank bank = questionBankService.getById(bankId);
            //在题库中的(单选,多选,判断题)题目
            List<Question> questions = questionService.list(new QueryWrapper<Question>().like("qu_bank_name", bank.getBankName()).in("qu_type", 1,2,3));
            //构造前端需要的vo对象
            List<QuestionVo> questionVos = new ArrayList<>();
            for (Question question : questions) {
                QuestionVo questionVo = new QuestionVo();

                questionVo.setQuestionId(question.getId());
                questionVo.setQuestionLevel(question.getLevel());
                if (question.getImage() != null && !question.getImage().equals("")) //防止没有图片对象
                    questionVo.setImages(question.getImage().split(","));
                questionVo.setCreatePerson(question.getCreatePerson());
                questionVo.setAnalysis(question.getAnalysis());
                questionVo.setQuestionContent(question.getQuContent());
                questionVo.setQuestionType(question.getQuType());

                Answer answer = answerService.getOne(new QueryWrapper<Answer>().eq("question_id", question.getId()));
                //选项个数
                String[] options = answer.getAllOption().split(",");
                String[] images = answer.getImages().split(",");
                //构造答案对象
                QuestionVo.Answer[] handleAnswer = new QuestionVo.Answer[options.length];
                //字段处理
                for (int i = 0; i < options.length; i++) {
                    QuestionVo.Answer answer1 = new QuestionVo.Answer();
                    if (images.length-1 >= i && images[i] != null && !images[i].equals(""))
                        answer1.setImages(new String[]{images[i]});
                    answer1.setAnswer(options[i]);
                    answer1.setId(i);
                    answer1.setIsTrue("false");
                    handleAnswer[i] = answer1;
                }
                if (question.getQuType() != 2) {//单选和判断
                    int trueOption = Integer.parseInt(answer.getTrueOption());
                    handleAnswer[trueOption].setIsTrue("true");
                    handleAnswer[trueOption].setAnalysis(answer.getAnalysis());
                } else {//多选
                    String[] trueOptions = answer.getTrueOption().split(",");
                    for (String trueOption : trueOptions) {
                        handleAnswer[Integer.parseInt(trueOption)].setIsTrue("true");
                        handleAnswer[Integer.parseInt(trueOption)].setAnalysis(answer.getAnalysis());
                    }
                }
                questionVo.setAnswer(handleAnswer);
                questionVos.add(questionVo);
            }
//            redisUtil.set("questionBankQuestion:" + bankId, questionVos, 60 * 5 + new Random().nextInt(2));
            return new CommonResult<>(200, "当前题库题目查询成功", questionVos);
        }
    }

    @GetMapping("/getQuestionByBankIdAndType")
    @ApiOperation("根据题库id和题目类型获取题目信息 type(1单选 2多选 3判断)")
    public CommonResult<List<QuestionVo>> getQuestionByBankIdAndType(Integer bankId,Integer type){
        //调用根据题库查询所有题目信息的方法
        CommonResult<Object> questionByBank = getQuestionByBank(bankId);
        List<QuestionVo> questionVos = (List<QuestionVo>)questionByBank.getData();
        //根据题目类型筛选题目
        questionVos.removeIf(questionVo -> !Objects.equals(questionVo.getQuestionType(), type));
        return new CommonResult<>(200,"根据题目类型查询成功",questionVos);
    }
}
