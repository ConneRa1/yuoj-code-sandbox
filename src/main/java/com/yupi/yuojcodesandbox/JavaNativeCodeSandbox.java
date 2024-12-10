/**
 * copyright (C), 2015-2024
 * fileName: JavaNativeCodeSandbox
 *
 * @author: mlt
 * date:    2024/12/2 下午6:32
 * description:
 * history:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 * adpost    2024/12/2 下午6:32           V1.0
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
 *
 *
 * @author mlt
 * @version 1.0.0
 * @date 2024/12/2
 */
@Slf4j
@Component
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate {
    @Override
    public ExcuteReponse excuteCode(ExcuteRequest request) {
        return super.excuteCode(request);
    }

/*    public static void main(String[] args) {
        JavaNativeCodeSandbox javaNativeCodeSandbox=new JavaNativeCodeSandbox();
        ExcuteRequest excuteRequest = new ExcuteRequest();
        excuteRequest.setInputList(Arrays.asList("1 2","2 3"));
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
        //String code = ResourceUtil.readStr("testCode/ErrorCode/RunProcessError.java", StandardCharsets.UTF_8);
        excuteRequest.setCode(code);
        excuteRequest.setLanguage("java");
        ExcuteReponse excuteReponse = javaNativeCodeSandbox.excuteCode(excuteRequest);
        log.info(excuteReponse.toString());
    }*/
}
