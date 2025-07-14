package com.sba.authentications.services;


import com.sba.admissions.pojos.AdmissionTickets;
import com.sba.model.EmailDetail;
import com.sba.authentications.repositories.AuthenticationRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;


@Service
public class EmailService {

    private final TemplateEngine templateEngine;

    private final JavaMailSender javaMailSender;

    private final AuthenticationRepository authenticationRepository;

    public EmailService(TemplateEngine templateEngine, JavaMailSender javaMailSender, AuthenticationRepository authenticationRepository) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
        this.authenticationRepository = authenticationRepository;
    }

    public void sendMailTemplate(EmailDetail emailDetail) {
        try {
            // 1. Lấy dữ liệu bổ sung
            Map<String, Object> extra = emailDetail.getExtra();   // cần có getExtra()

            // 2. Tạo context cho Thymeleaf
            Context context = new Context();
            context.setVariable("username", emailDetail.getName());
            context.setVariable("email",   emailDetail.getRecipient());

            // --- Các biến riêng cho template phản hồi ticket ---
            if (extra != null && extra.get("ticket") instanceof AdmissionTickets ticket) {
                context.setVariable("topic",     ticket.getTopic());
                context.setVariable("content",   ticket.getContent());
                context.setVariable("response",  ticket.getResponse());
            }

            // Nếu dùng chung hàm cho nhiều template, bạn có thể kiểm tra tên template:
            // 3. Sinh HTML
            String template = emailDetail.getTemplate() != null
                    ? emailDetail.getTemplate()
                    : "response-ticket-template";
            String html = templateEngine.process(template, context);
            // 4. Gửi mail
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("swpproject2024@gmail.com");
            helper.setTo(emailDetail.getRecipient());
            helper.setSubject(emailDetail.getSubject());
            helper.setText(html, true);

            // Đính kèm (nếu có)
            if (emailDetail.getAttachment() != null) {
                helper.addAttachment(emailDetail.getAttachment().getFilename(),
                        emailDetail.getAttachment());
            }

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }





    public void sendMailTemplateForgot(EmailDetail emailDetail) {
        try {
            // Log recipient email address for debugging
            System.out.println("Sending email to: " + emailDetail.getRecipient());

            // Validate email address
            if (emailDetail.getRecipient() == null || emailDetail.getRecipient().isEmpty()) {
                throw new IllegalArgumentException("Recipient email address is invalid or empty");
            }

            Context context = new Context();
            context.setVariable("username", emailDetail.getName());
            context.setVariable("buttonValue", emailDetail.getButtonValue());
            context.setVariable("link", emailDetail.getLink());
            context.setVariable("email", emailDetail.getRecipient());
            context.setVariable("registrationDate", java.time.Clock.systemUTC().instant());

            String text = templateEngine.process("forgotpasswordemailtemplate", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom("swpproject2024@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setText(text, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());

            if (emailDetail.getAttachment() != null) {
                mimeMessageHelper.addAttachment(emailDetail.getAttachment().getFilename(), emailDetail.getAttachment());
            }

            javaMailSender.send(mimeMessage);

            // Log success
            System.out.println("Email successfully sent to: " + emailDetail.getRecipient());

        } catch (MessagingException messagingException) {
            messagingException.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
