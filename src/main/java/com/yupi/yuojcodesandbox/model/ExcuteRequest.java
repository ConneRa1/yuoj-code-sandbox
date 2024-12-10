/**
 * copyright (C), 2015-2024
 * fileName: ExcuteRequest
 *
 * @author: mlt
 * date:    2024/11/22 下午4:24
 * description: 沙箱执行请求
 * history:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 * adpost    2024/11/22 下午4:24           V1.0        沙箱执行请求
 */
package com.yupi.yuojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 沙箱执行请求
 *
 * @author mlt
 * @version 1.0.0
 * @date 2024/11/22
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExcuteRequest implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     *  输入list
     */
    private List<String> inputList;
    /**
     * code
     */
    private String code;
    /**
     * language
     */
    private String language;
}
