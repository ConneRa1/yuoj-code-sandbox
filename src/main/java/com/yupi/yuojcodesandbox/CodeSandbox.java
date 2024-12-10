/**
 * copyright (C), 2015-2024
 * fileName: CodeSandbox
 *
 * @author: mlt
 * date:    2024/11/22 下午4:22
 * description:
 * history:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 * adpost    2024/11/22 下午4:22           V1.0
 */
package com.yupi.yuojcodesandbox;


import com.yupi.yuojcodesandbox.model.ExcuteReponse;
import com.yupi.yuojcodesandbox.model.ExcuteRequest;

/**
 * 代码沙箱接口
 *
 * @author mlt
 * @version 1.0.0
 * @date 2024/11/22
 */
public interface CodeSandbox {
    /**
     * 沙箱执行代码
     * @param request
     * @return
     */
    ExcuteReponse excuteCode(ExcuteRequest request);
}
