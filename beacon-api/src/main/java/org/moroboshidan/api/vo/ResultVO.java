package org.moroboshidan.vo;

import lombok.Data;

/**
 * 响应数据
 */
@Data
public class ResultVO {
    private Integer code;

    private String msg;

    private Integer count;

    private Long fee;

    private String uid;

    private String sid;
}
