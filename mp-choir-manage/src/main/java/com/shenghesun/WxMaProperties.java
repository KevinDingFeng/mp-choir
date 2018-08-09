package com.shenghesun;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;


@ConfigurationProperties(prefix = "wechat.miniapp")
@Data
public class WxMaProperties {

    private String appid;
    private String secret;
    private String token;
    private String aesKey;
    private String msgDataFormat;



    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
