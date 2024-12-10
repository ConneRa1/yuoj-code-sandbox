/**
 * copyright (C), 2015-2024
 * fileName: JavaCodeSandboxTemplate
 *
 * @author: mlt
 * date:    2024/12/5 下午4:07
 * description: java代码沙箱模版方法
 * history:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 * adpost    2024/12/5 下午4:07           V1.0        java代码沙箱模版方法
 */
package com.yupi.yuojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.WordTree;
import com.yupi.yuojcodesandbox.model.ExcuteReponse;
import com.yupi.yuojcodesandbox.model.ExcuteRequest;
import com.yupi.yuojcodesandbox.model.ExecuteMessage;
import com.yupi.yuojcodesandbox.model.JudgeInfo;
import com.yupi.yuojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * java代码沙箱模版方法
 *
 * @author mlt
 * @version 1.0.0
 * @date 2024/12/5
 */
@Slf4j
@Component
public class JavaCodeSandboxTemplate implements  CodeSandbox{

    private static final String GLOBAL_TMP_CODE_PATH = "tmp";
    private static final String JAVA_FILE_NAME = "Main.java";


    protected File saveCode(String code){
        String userParentRoot = System.getProperty("user.dir");
        String codeParentRoot = userParentRoot + File.separator +GLOBAL_TMP_CODE_PATH;
        //判断路径是否存在
        if(!FileUtil.exist(codeParentRoot))
        {
            FileUtil.mkdir(codeParentRoot);
        }
        // 放到用户对应的目录下
        String userCodeParentRoot = codeParentRoot + File.separator + UUID.randomUUID().toString();
        String userCodePath=userCodeParentRoot + File.separator+ JAVA_FILE_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, "UTF-8");
        return userCodeFile;
    }

    private ExcuteReponse compileCode(File userCodeFile){
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        Process process = null;
        ExecuteMessage executeMessage=null;
        try {
            process = Runtime.getRuntime().exec(compileCmd);
            // 得到编译信息
            executeMessage = ProcessUtils.runProcessAndGetMessage(process, compileCmd);
        } catch (IOException e) {
            return getErrorResponse(e);
        }
        return null;
    }


    protected List<ExecuteMessage> getOutputList(List<String >inputList,File userCodeFile){
        List<ExecuteMessage> messages=new ArrayList<>();
        String userCodeParentRoot = userCodeFile.getParentFile().getAbsolutePath();
        ExecuteMessage executeMessage=null;
        for(String input:inputList)
        {
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentRoot,
                    input);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                new Thread(()->{
                    try{
                        Thread.sleep(5000);
                        runProcess.destroy();
                    }
                    catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }).start();
                // 指定args
                executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, runCmd);
                // 交互式args
                //executeMessage = ProcessUtils.runInteractProcessAndGetMessage(runProcess, runCmd);
                log.info(executeMessage.getMessage());
                messages.add(executeMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return messages;
    }

    protected ExcuteReponse combineResponse(List<ExecuteMessage> messages,List<String >inputList,String language){
        ExcuteReponse excuteReponse = new ExcuteReponse();
        List<String> outputList=new ArrayList<>();
        Long maxTime= 0L;
        for(ExecuteMessage message:messages)
        {
            // 有错误信息
            if(StrUtil.isNotBlank(message.getErrorMessage())) {
                excuteReponse.setStatus(3);
                excuteReponse.setMessage(message.getErrorMessage());
                break;
            }
            outputList.add(message.getMessage());
            if(message.getTime()>maxTime) {
                maxTime=message.getTime();
            }

        }
        if(outputList.size()==inputList.size()) {
            excuteReponse.setStatus(1);
        }
        excuteReponse.setOutputList(outputList);
        excuteReponse.setLanguage(language);
        JudgeInfo judgeInfo=new JudgeInfo();
        judgeInfo.setTime(maxTime);
        excuteReponse.setJudgeInfo(judgeInfo);
        excuteReponse.setMessage("success");
        return excuteReponse;
    }


    protected Boolean cleanCode(File userCodeFile){
        String userCodeParentRoot = userCodeFile.getParentFile().getAbsolutePath();
        if(FileUtil.exist(userCodeParentRoot)) {
            boolean del = FileUtil.del(userCodeParentRoot);
            if(!del) {
                log.info("删除失败");
            }
            return del;
        }
        return false;
    }


    @Override
    public ExcuteReponse excuteCode(ExcuteRequest request) {
        List<String> inputList = request.getInputList();
        String code = request.getCode();
        String language = request.getLanguage();
        // 将code保存到tmp目录下
        File userCodeFile = saveCode(code);
        // 编译code
        ExcuteReponse compileResponse = compileCode(userCodeFile);
        if(compileResponse != null){
            return compileResponse;
        }
        // 获取执行信息
        List<ExecuteMessage> messages=getOutputList(inputList,userCodeFile);
        // 拼返回
        ExcuteReponse excuteReponse = combineResponse(messages, inputList, language);
        //清理对象
        Boolean b = cleanCode(userCodeFile);

        return excuteReponse;
    }


    private ExcuteReponse getErrorResponse(Throwable e) {
        ExcuteReponse excuteReponse = new ExcuteReponse();
        excuteReponse.setOutputList(null);
        excuteReponse.setLanguage(null);
        // 编译未通过
        excuteReponse.setStatus(2);
        excuteReponse.setJudgeInfo(null);
        excuteReponse.setMessage(null);
        return excuteReponse;
    }
    private ExcuteReponse getFliterResponse() {
        ExcuteReponse excuteReponse = new ExcuteReponse();
        excuteReponse.setOutputList(null);
        excuteReponse.setLanguage(null);
        excuteReponse.setStatus(4);
        excuteReponse.setJudgeInfo(null);
        excuteReponse.setMessage(null);
        return excuteReponse;
    }

//    public static void main(String[] args) {
//        JavaCodeSandboxTemplate javaNativeCodeSandbox=new JavaCodeSandboxTemplate();
//        ExcuteRequest excuteRequest = new ExcuteRequest();
//        excuteRequest.setInputList(Arrays.asList("1 2","2 3"));
//        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
//        //String code = ResourceUtil.readStr("testCode/ErrorCode/RunProcessError.java", StandardCharsets.UTF_8);
//        excuteRequest.setCode(code);
//        excuteRequest.setLanguage("java");
//        ExcuteReponse excuteReponse = javaNativeCodeSandbox.excuteCode(excuteRequest);
//        log.info(excuteReponse.toString());
//    }
}
