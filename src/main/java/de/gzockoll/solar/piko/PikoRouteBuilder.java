package de.gzockoll.solar.piko;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.util.toolbox.AggregationStrategies;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
//@Component
public class PikoRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        getContext().setTracing(true);
        Map params= ImmutableMap.builder().put("edWrNr",1).build();
        from("seda:readPiko")
                .to("http4://192.168.187.39?nocache&authMethod=Basic&authUsername=pvserver&authPassword=pvwr&bridgeEndpoint=true")
                .unmarshal().tidyMarkup();

        from("servlet:///solar/momentan")
                .to("seda:readPiko").to("direct:momentan");

        from("direct:momentan")
                .setHeader("Content",constant("momentan"))
                .setBody().xpath("/html/body/form/table[3]/tr[4]/td[3]/text()").convertBodyTo(String.class);

        from("servlet:///solar/tag")
                .to("seda:readPiko").to("direct:tag");

        from("direct:tag")
                .setHeader("Content", constant("tag"))
                .setBody().xpath("/html/body/form/table[3]/tr[6]/td[6]/text()").convertBodyTo(String.class);

        from("servlet:///solar/gesamt")
                .to("seda:readPiko").to("direct:gesamt");

        from("direct:gesamt")
                .setHeader("Content", constant("gesamt"))
                .setBody().xpath("/html/body/form/table[3]/tr[4]/td[6]/text()")
                .convertBodyTo(String.class);

        from("servlet:///solar/daten")
                .to("seda:readPiko")
                .multicast(AggregationStrategies.beanAllowNull(
                        new Aggregator(), "aggregate"))
                    .to("direct:momentan", "direct:tag", "direct:gesamt")
                .end();

        from("timer://foo?period=30000")
                .setExchangePattern(ExchangePattern.InOut)
                .to("seda:readPiko")
                .multicast()
                    .to("direct:setMomentan", "direct:setTag", "direct:setGesamt")
                .end();

        from("direct:setMomentan")
                .to("log:bla?showAll=true&multiline=true")
                .to("direct:momentan")
                .to("mqtt:bar?host=tcp://docker:1883&publishTopicName=piko/status/Momentan_Leistung");

        from("direct:setTag")
                .to("direct:tag")
                .to("mqtt:bar?host=tcp://docker:1883&publishTopicName=piko/status/Tages_Energie");

        from("direct:setGesamt")
                .to("direct:gesamt")
                .to("mqtt:bar?host=tcp://docker:1883&publishTopicName=piko/status/Gesamt_Energie");

        from("mqtt:bar?host=tcp://docker:1883&subscribeTopicName=hm/status/Temperatur Sensor innen/TEMPERATURE").transform(body().convertToString()).to("mock:result");
    }
}