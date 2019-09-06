package com.dt.pyg.ssm;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** SpringBoot引导类 */
@SpringBootApplication
public class Application {
    public static void main(String[] args){
        /** 创建SpringApplication */
        SpringApplication springApplication =
                new SpringApplication(Application.class);
        /** 设置Banner模式为关闭 */
        springApplication.setBannerMode(Banner.Mode.OFF);
        /** 运行 */
        springApplication.run(args);
    }

}
