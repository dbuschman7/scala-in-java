package me.lightspeed7.scalaInJava;

import org.junit.Test;

public class TestHarness {

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
            new DispatchScala("http://cayman-vm:8161", "admin", "admin").send(mailMessage);

            // Java Async-Http-Client
            new JavaClient("http://cayman-vm:8161", "admin", "admin").sendAsyncHttpClient(mailMessage);

            // Scala Dispatch From Java
            new JavaClient("http://cayman-vm:8161", "admin", "admin").sendDispatchScala(mailMessage);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
