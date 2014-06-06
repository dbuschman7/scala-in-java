package me.lightspeed7.scalaInJava;

import org.slf4j.LoggerFactory;

import scala.Tuple2;
import scala.collection.immutable.Map;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Realm;
import com.ning.http.client.Realm.AuthScheme;
import com.ning.http.client.Realm.RealmBuilder;
import com.ning.http.client.Request;
import com.ning.http.client.Response;

import dispatch.Http;
import dispatch.Req;

public class MailerRestClient {

    public static final org.slf4j.Logger log = LoggerFactory.getLogger(MailerRestClient.class);
    private String baseUrl;
    private String username;
    private String password;

    public MailerRestClient(String baseUrl, String username, String password) {

        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;

    }

    public void sendJava(MailMessage mailMessage)
            throws Exception {

        try (AsyncHttpClient asyncHttpClient = new AsyncHttpClient()) {

            // setup the query
            Realm realm = new RealmBuilder()//
                    .setPrincipal(username).setPassword(password)//
                    .setScheme(AuthScheme.DIGEST).build();

            ListenableFuture<Response> f = asyncHttpClient//
                    .preparePost(baseUrl + "/api/message/EMAIL.QUEUE?type=queue") //
                    .setHeader("Content-Type", "application/x-www-form-urlencoded")//
                    .setRealm(realm)//
                    .setBody(String.format("body=%s", mailMessage.marshal())) //
                    .execute();

            // wait for the response
            Response response = f.get();
            if (200 != response.getStatusCode()) {
                throw new Exception("ActiveMQ returned statusCode = " + response.getStatusCode());
            }

            // print out the response body
            String body = response.getResponseBody();
            System.out.println("body=" + body);
        }

    }

    public void sendScala(MailMessage mailMessage)
            throws Exception {

        // setup the query
        Tuple2<String, String> tuple = new Tuple2<String, String>("body", mailMessage.marshal());

        @SuppressWarnings( "unchecked" )
        Map<String, String> map = scala.collection.immutable.Map$.MODULE$.<String, String> newBuilder()//
                .$plus$eq(tuple)//
                .result();

        Req req = dispatch.url.apply(baseUrl + "/api/message/EMAIL.QUEUE?type=queue")//
                .as(username, password)//
                .setHeader("Content-Type", "application/x-www-form-urlencoded")//
                .$less$less(map)//
        ;

        // do the request
        Request request = req.toRequest();
        System.out.println(request.toString());
        ListenableFuture<Response> listenableFuture = Http.apply$default$1().executeRequest(request);

        // do the future
        Response response = listenableFuture.get();
        if (200 != response.getStatusCode()) {
            throw new Exception("ActiveMQ returned statusCode = " + response.getStatusCode());
        }

        // print out the response body
        String body = response.getResponseBody();
        System.out.println("body=" + body);
    }
}
