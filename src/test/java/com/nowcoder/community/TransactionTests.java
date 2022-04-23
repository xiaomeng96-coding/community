package com.nowcoder.community;

import com.nowcoder.community.service.AlphaServiceImpl;
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
public class TransactionTests {

    private static final Logger logger = LoggerFactory.getLogger(TransactionTests.class);

    @Autowired
    private AlphaServiceImpl alphaService;

    @Test
    public void testSave() {
        alphaService.save();
        logger.info("save success");
    }

    @Test
    public void testSaveTransaction() {
        alphaService.saveTransaction();
        logger.info("save success");
    }
}
