/**
 * copyright (C), 2015-2024
 * fileName: ExecuteMessage
 *
 * @author: mlt
 * date:    2024/12/3 上午11:33
 * description: 执行信息
 * history:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 * adpost    2024/12/3 上午11:33           V1.0        执行信息
 */
package com.yupi.yuojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 执行信息
 *
 * @author mlt
 * @version 1.0.0
 * @date 2024/12/3
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteMessage  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * exit code
     */
    private Integer exitValue;
    /**
     * 执行信息
     */
    private String message;
    /**
     * 报错信息
     */
    private String errorMessage;
    /**
     * time
     */
    private Long time;
}
