package com.gastrobar_alemans_backend.service;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service

public class MailService {
    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${from.mail}")
    private String from;

    public void sendCode(String to, String code) throws Exception {
        Mail mail = new Mail(
                new Email(from),
                "Código de recuperación - Gastrobar Alemán",
                new Email(to),
                new Content("text/plain", "Tu código de 6 dígitos es: " + code + "\nVálido por 10 minutos.")
        );
        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        sg.api(request);
    }
}
