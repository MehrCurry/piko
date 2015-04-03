package de.gzockoll.solar.piko;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Service;

@Service
public class PikoRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("sed:readPiko")
                .to("http4://192.168.187.39?nocache&amp;authMethod=Basic&amp;authUsername=pvserver&amp;authPassword=pvwr&amp;bridgeEndpoint=true")
                .unmarshal().tidyMarkup();

        from("jetty:http://0.0.0.0:11145/solar/momentan")
                .to("seda:readPiko").setBody().xpath("/html/body/form/table[3]/tr[4]/td[3]/text()");

        from("jetty:http://0.0.0.0:11145/solar/tag")
                .to("seda:readPiko")
                .setBody().xpath("/html/body/form/table[3]/tr[6]/td[6]/text()");

        from("jetty:http://0.0.0.0:11145/solar/gesamt")
                .to("seda:readPiko")
                .setBody().xpath("/html/body/form/table[3]/tr[4]/td[6]/text()");

    }
}
