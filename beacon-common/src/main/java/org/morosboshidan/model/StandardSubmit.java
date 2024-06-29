package org.morosboshidan.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 贯穿接口模块、策略模块、短信网关模块，需要做校验和封装的POJO类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardSubmit implements Serializable {
    /**
     * 针对当前短信的唯一标识，雪花算法
     */
    private Long sequenceId;

    /**
     * 客户端ID，基于apikey查询缓存模块得到客户的ID
     */
    private Long clientId;

    /**
     * 客户端的ip白名单，查询缓存
     */
    private List<String> ip;

    /**
     * 客户业务内的uid，客户请求传递的
     */
    private String uid;

    /**
     * 目标手机号，客户请求传递的
     */
    private String mobile;

    /**
     * 短信内容的签名，客户请求传递的，只需要在短信内容中基于【】获取出来
     */
    private String sign;

    /**
     * 短信内容，客户请求传递的
     */
    private String text;

    // /**
    //  * 短信的发送时间，当前系统时间
    //  */
    // @JsonSerialize(using = LocalDateTimeSerializer.class)
    // @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime sendTime;


    /**
     * 当前短信的费用，计算短信内容的文字，70个字一条，超过部分，67个字一条,单位（厘）
     */
    private Long fee;

    /**
     * 目标手机号的运营商（策略模块）
     */
    private Integer operatorId;


    /**
     * 目标手机号的归属地区号  0451  0455 （策略模块，三方查询不到，先不管）
     */
    private Integer areaCode;

    /**
     * 目标手机号的归属地  哈尔滨，  绥化~   （策略模块）
     */
    private String area;

    /**
     * 通道下发的源号码  106934985673485645  （策略模块）
     */
    private String srcNumber;

    /**
     * 通道的id信息   （策略模块）
     */
    private Long channelId;

    /**
     * 短信的发送状态， 0-等待/发送ing，1-成功，2-失败 ，默认情况就是0
     */
    private int reportState;

    /**
     * 短信发送失败的原因是什么，记录在当前属性
     */
    private String errorMsg;

    // =============================================================================
    /**
     *  获取到的客户端真实IP地址
     */
    private String realIP;

    /**
     * 客户端请求携带的apiKey
     */
    private String apikey;

    /**
     *  0-验证码短信 1-通知类短信 2-营销类短信
     */
    private int state;

    /**
     * 签名的id
     */
    private Long signId;

    /**
     * 是否携号转网，  isTransfer = true，代表做了携号转网的判断并且做了操作
     */
    private Boolean isTransfer = false;

    /**
     *  针对1小时限流规则存储的系统时间毫秒值
     */
    private Long oneHourLimitMilli;

    // 后续再做封装~~~~
}
