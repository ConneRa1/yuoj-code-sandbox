/**
 * copyright (C), 2015-2024
 * fileName: ExcuteReponse
 *
 * @author: mlt
 * date:    2024/11/22 下午4:23
 * description: 沙箱执行响应
 * history:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 * adpost    2024/11/22 下午4:23           V1.0        沙箱执行响应
 */
package com.yupi.yuojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 沙箱执行响应
 *
 * @author mlt
 * @version 1.0.0
 * @date 2024/11/22
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExcuteReponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     *  输出list
     */
    private List<String> outputList;
    /**
     * language
     */
    private String language;
    //status
    private Integer status;
    //JudgeInfo
    private JudgeInfo judgeInfo;
    //message   -> 处理系统的信息
    private String message;

}
