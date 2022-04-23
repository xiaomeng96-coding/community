package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author : zhiHao
 * @since : 2021/9/25
 */
@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String originator;

    public void sendMail(String addressee, String subject, String content) {
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(originator);
            helper.setTo(addressee);
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(helper.getMimeMessage());
        }catch (MessagingException e) {
            logger.error("发送邮件失败：" + e.getMessage());
        }
    }

}
