package com.aitor.api_tfg.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Verifica tu cuenta";

        String verificationUrl = "http://192.168.1.230:8080/auth/verify?token=" + token;

        String htmlContent = """
            <html>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', sans-serif; background-color: #f9f9f9;">
                <div style="max-width: 600px; margin: 30px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 30px;">
                   \s
                    <div style="text-align: center;">
                        <img src="https://i.postimg.cc/vBMsG01L/Logo-Track-Fit.png" alt="Logo" style="width: 100px; height: auto; margin-bottom: 20px;">
                        <h2 style="color: #333;">¡Bienvenido a TrackFit!</h2>
                    </div>
                   \s
                    <p style="font-size: 16px; color: #555;">
                        Gracias por registrarte. Para comenzar, por favor verifica tu cuenta haciendo clic en el botón siguiente:
                    </p>
                   \s
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #F24822; color: #ffffff; padding: 14px 24px; text-decoration: none; border-radius: 6px; font-size: 16px;">
                            Verificar Cuenta
                        </a>
                    </div>
                   \s
                    <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                   \s
                    <p style="font-size: 13px; color: #999; text-align: center;">
                        Este correo fue enviado automáticamente. Si no creaste una cuenta, puedes ignorarlo.<br>
                        © 2025 TrackFit. Todos los derechos reservados.
                    </p>
                </div>
            </body>
            </html>
           \s""".formatted(verificationUrl, verificationUrl, verificationUrl);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = contenido HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }

}

