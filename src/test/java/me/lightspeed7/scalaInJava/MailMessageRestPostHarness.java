package me.lightspeed7.scalaInJava;

import org.junit.Test;

public class MailMessageRestPostHarness {

    boolean scala = true;

    @Test
    // @Ignore
    public void testSendBean() {

        MailMessage mailMessage = new MailMessage();
        mailMessage.setBody("test");
        mailMessage.setSubject("test email");
        mailMessage.setFromAddress("noreply@transzap.com");
        mailMessage.setToAddress("dbusch@transzap.com");

        try {
            // Scala Dispatch Library
            new MailerRestHelper("http://cayman-vm:8161", "admin", "admin").send(mailMessage);

            // Java Async-Http-Client
            new MailerRestClient("http://cayman-vm:8161", "admin", "admin").sendJava(mailMessage);

            // Scala Dispatch From Java
            new MailerRestClient("http://cayman-vm:8161", "admin", "admin").sendScala(mailMessage);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
