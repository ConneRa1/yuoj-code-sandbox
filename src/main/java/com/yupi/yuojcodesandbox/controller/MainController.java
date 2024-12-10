/**
 * copyright (C), 2015-2024
 * fileName: MainController
 *
 * @author: mlt
 * date:    2024/12/2 下午6:02
 * description:
 * history:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 * adpost    2024/12/2 下午6:02           V1.0
 */
package com.yupi.yuojcodesandbox.controller;

import com.yupi.yuojcodesandbox.JavaDockerCodeSandbox;
import com.yupi.yuojcodesandbox.JavaNativeCodeSandbox;
import com.yupi.yuojcodesandbox.model.ExcuteReponse;
import com.yupi.yuojcodesandbox.model.ExcuteRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 *
 * @author mlt
 * @version 1.0.0
 * @date 2024/12/2
 */

@RestController
@AllArgsConstructor
public class MainController {
    private final JavaDockerCodeSandbox javaDockerCodeSandbox;
    //private final JavaNativeCodeSandbox javaNativeCodeSandbox;
    private final String AUTH_KEY="key";
    private final String AUTH_SECRET="secret";

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/execute")
    public ExcuteReponse execute(@RequestBody ExcuteRequest excuteRequest, HttpServletRequest request,
                                 HttpServletResponse response) {
        // 简单鉴权
        String key = request.getHeader(AUTH_KEY);
        if(!key.equals(AUTH_SECRET)) {
            response.setStatus(403);
            return null;
        }
        if(excuteRequest == null) {
            System.out.println("请求参数为空");
            return null;
        }
        return  javaDockerCodeSandbox.excuteCode(excuteRequest);
    }
}
