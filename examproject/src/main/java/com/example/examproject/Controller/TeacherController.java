package com.example.examproject.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.examproject.Pojo.CommonResult;
import com.example.examproject.Pojo.Question;
import com.example.examproject.Pojo.QuestionBank;
import com.example.examproject.Service.impl.QuestionBankServiceImpl;
import com.example.examproject.Service.impl.QuestionServiceImpl;
import com.example.examproject.Service.impl.UserRoleServiceImpl;
import com.example.examproject.Service.impl.UserServiceImpl;
import com.example.examproject.Utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
