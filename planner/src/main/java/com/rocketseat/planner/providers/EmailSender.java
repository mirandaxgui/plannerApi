package com.rocketseat.planner.providers;


import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class EmailSender {

    public static void sendEmail(String to, String subject, String body) {
        // Configurações do servidor SMTP
        String host = "smtp.gmail.com"; // Altere para o seu servidor SMTP
        final String username = "gui.plannersender@gmail.com"; // Seu e-mail
        final String password = "bohp vhfw capo nyej"; // Sua senha

        // Propriedades da conexão
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587"); // Porta comum para TLS

        // Criação da sessão
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Criação da mensagem
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Envio da mensagem
            Transport.send(message);
            System.out.println("E-mail enviado com sucesso!");

        } catch (MessagingException e) {
          System.err.println("Erro ao enviar o e-mail: " + e.getMessage());
      }
      
    }
}


