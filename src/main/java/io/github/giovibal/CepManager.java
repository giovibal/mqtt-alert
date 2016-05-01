package io.github.giovibal;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.InputStream;
import java.util.Date;

/**
 * Created by giovibal on 24/04/16.
 */
public class CepManager {
    private KieServices kieServices;
    private KieFileSystem kfs;
    private KieBuilder kieBuilder;
    private KieContainer kieContainer;
    private KieBase kieBase;
    private KieSession kieSession;
    private KieBaseConfiguration kieBaseConfiguration;

    public CepManager() {
        InputStream fis = getClass().getResourceAsStream("/cep.drl");
        initRuleEngine(fis);
    }



    public void initRuleEngine(InputStream fis) {
        kieServices = KieServices.Factory.get();
        kfs = kieServices.newKieFileSystem();
        Resource res = kieServices.getResources().newInputStreamResource(fis);
        kfs.write( "src/main/resources/cep.drl", res);
        kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        ReleaseId relId = kieBuilder.getKieModule().getReleaseId();
        kieContainer = kieServices.newKieContainer(relId);
        kieBaseConfiguration = kieServices.newKieBaseConfiguration();
        kieBaseConfiguration.setOption( EventProcessingOption.STREAM );
        kieBase = kieContainer.newKieBase(kieBaseConfiguration);

        kieSession = kieBase.newKieSession();
        kieSession.setGlobal("engine", this);

        new Thread(new Runnable() {
            public void run() {
                kieSession.fireUntilHalt();
            }
        }).start();
    }

    public void insertEvent(PirEvent evt) {
        kieSession.insert(evt);
    }

    public void setGlobal(String varName, Object obj) {
        kieSession.setGlobal(varName, obj);
    }


    public void logEvent(String msg) {
        String s = String.format("[%s]: %s", new Date(), msg);
        System.out.println(s);

//        try {
//            MqttClient mqtt = new MqttClient("tcp://104.154.60.150:1883")
//            mqtt.publish("/baleani/laspio/logs", s);
//        } catch(Throwable e ) {}
    }
}
