package com.template.webserver;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.utilities.NetworkHostAndPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Value("${mint.host}")
    private String mintHostAndPort;

    @Value("${traderA.host}")
    private String traderAHostAndPort;

    @Value("${traderB.host}")
    private String traderBHostAndPort;

    @Bean(destroyMethod = "")  // Avoids node shutdown on rpc disconnect
    public CordaRPCOps mintProxy(){
        CordaRPCClient partyCClient = new CordaRPCClient(NetworkHostAndPort.parse(mintHostAndPort));
        return partyCClient.start("user1", "test").getProxy();
    }

    @Bean(destroyMethod = "")  // Avoids node shutdown on rpc disconnect
    public CordaRPCOps traderAProxy(){
        CordaRPCClient partyAClient = new CordaRPCClient(NetworkHostAndPort.parse(traderAHostAndPort));
        return partyAClient.start("user1", "test").getProxy();
    }

    @Bean(destroyMethod = "")
    public CordaRPCOps traderBProxy(){
        CordaRPCClient partyBClient = new CordaRPCClient(NetworkHostAndPort.parse(traderBHostAndPort));
        return partyBClient.start("user1", "test").getProxy();
    }

    /**
     * Corda Jackson Support, to convert corda objects to json
     */
//    @Bean
//    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(){
//        ObjectMapper mapper =  JacksonSupport.createDefaultMapper(partyAProxy());
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setObjectMapper(mapper);
//        return converter;
//    }
}

