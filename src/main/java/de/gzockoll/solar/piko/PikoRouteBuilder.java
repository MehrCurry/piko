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

/*
        <route id="readPiko">
            <from uri="seda:readPiko" />
            <to uri="http4://192.168.187.39?nocache&amp;authMethod=Basic&amp;authUsername=pvserver&amp;authPassword=pvwr&amp;bridgeEndpoint=true" />
            <unmarshal>
                <tidyMarkup/>
            </unmarshal>
        </route>
        <route id="route1">
            <from uri="jetty:http://0.0.0.0:11145/solar/momentan" />
            <to uri="seda:readPiko" />
            <setBody>
                <xpath>/html/body/form/table[3]/tr[4]/td[3]/text()</xpath>
            </setBody>
        </route>
        <route id="route2">
            <from uri="jetty:http://0.0.0.0:11145/solar/tag" />
            <to uri="seda:readPiko" />
            <setBody>
                <xpath>/html/body/form/table[3]/tr[6]/td[6]/text()</xpath>
            </setBody>
        </route>
        <route id="route3">
            <from uri="jetty:http://0.0.0.0:11145/solar/gesamt" />
            <to uri="seda:readPiko" />
            <setBody>
                <xpath>/html/body/form/table[3]/tr[4]/td[6]/text()</xpath>
            </setBody>
        </route>


 */
