package camel.start;

import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultMessage;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


public class Starter {

    public static void main(String[] args) throws Exception {
        // create a CamelContext
        CamelContext camelContext = new DefaultCamelContext();

        ProducerTemplate template = camelContext.createProducerTemplate();
        DataSource dataSource = new DriverManagerDataSource(
                "jdbc:postgresql://localhost:5432/base?user=postgres&password=password"
        );

        camelContext.getRegistry().bind("base", dataSource);

        // add routes which can be inlined as anonymous inner class
        // (to keep all code in a single java file for this basic example)
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("timer:base?period=60000")
                        .routeId("JDBC route")
                        .setHeader("key" , constant(1))
                        .setBody(simple("select id, name from users where id > :?key"))
                        .to("jdbc:base?useHeadersAsParameters=true")
                        .log(">>>>>>>> ${body}")
                        .log("Hello Camel")
                        .process(exchange -> {
                            Message in = exchange.getIn();
                            Object body = in.getBody();
                            DefaultMessage message = new DefaultMessage(exchange);
                            message.setHeaders(in.getHeaders());
                            message.setHeader("myHeader", "hahahahaha");
                            message.setBody(body.toString() + "\n" + in.getHeaders().toString());
                            exchange.setMessage(message);
                        })
                        .toD("file://C:/Users/vchernyak/IdeaProjects/camel-start/files/toB?fileName=update-${date:now:yyyy-MM-dd}-${headers.myHeader}.txt");
            }
        });

//        camelContext.getPropertiesComponent().setLocation("classpath:app.properties");
//
//        camelContext.addRoutes(new RouteBuilder() {
//            @Override
//            public void configure() throws Exception {
//                from("file:{{from}}")
//                        .routeId("File processing")
////                        .log(">>>>>>>>>> ${body}")
//                        .convertBodyTo(String.class)
//                        .to("log:?showBody=true&showHeaders=true")
//                        .choice()
//                        .when(exchange -> {
//                            return ((String) exchange.getIn().getBody()).contains("=a");
//                        })
//                        .to("file:{{toA}}")
//                        .when(exchange -> ((String) exchange.getIn().getBody()).contains("=b"))
//                        .to("file:{{toB}}")
//                        .otherwise()
//                        .to("file:{{toB}}");
////                .end()
////                        .to("file:{{toA}}");
//            }
//        });

        camelContext.start();
String hello = "<h1>Hello world</h1>";
        template.sendBody(
                "file://C:/Users/vchernyak/IdeaProjects/camel-start/files/toA?fileName=hello-${date:now:dd-MM-dddd}.html",
                hello);
        Thread.sleep(2000);
        camelContext.start();


    }
}
