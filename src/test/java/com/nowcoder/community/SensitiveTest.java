package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


/**
 * @author : zhiHao
 * @since : 2022/4/20
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以赌博，可以开票，可以嫖娼，可以吸毒，哈哈哈";
        text = sensitiveFilter.filter(text);
        logger.info("text: " + text);
    }

}
