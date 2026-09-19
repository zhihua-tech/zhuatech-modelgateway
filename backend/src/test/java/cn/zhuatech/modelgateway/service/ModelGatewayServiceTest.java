/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */package cn.zhuatech.modelgateway.service;import cn.zhuatech.modelgateway.model.GatewayAudit;import cn.zhuatech.modelgateway.repository.GatewayAuditRepository;import org.junit.jupiter.api.*;import java.math.BigDecimal;import java.util.*;import static org.assertj.core.api.Assertions.assertThat;import static org.mockito.ArgumentMatchers.any;import static org.mockito.Mockito.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class ModelGatewayServiceTest{GatewayAuditRepository repo;OpenAiCompatibleClient client;ModelGatewayService service;/**
                                                                                                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                     */
@BeforeEach void init(){repo=mock(GatewayAuditRepository.class);client=mock(OpenAiCompatibleClient.class);service=new ModelGatewayService(repo,client);when(repo.findByRequestId(any())).thenReturn(Optional.empty());when(repo.save(any())).thenAnswer(i->i.getArgument(0));}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 ModelGatewayService.ModelTarget target(String name,double quality,long latency,BigDecimal cost,boolean healthy){return new ModelGatewayService.ModelTarget(name,"https://api.example.com","model",Set.of("chat"),Set.of("CN"),quality,latency,cost,cost,100000,healthy,true);}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Test void routesBestEligibleModel(){var r=new ModelGatewayService.RouteRequest("R1","hello","chat","CN",100,100,new BigDecimal("1"),true,false,List.of(target("fast",.90,100,new BigDecimal(".02"),true),target("quality",.98,300,new BigDecimal(".03"),true)));var a=service.route(r,"admin");assertThat(a.decision()).isEqualTo(ModelGatewayService.Decision.ROUTED);assertThat(a.selectedModel()).isEqualTo("quality");assertThat(a.fallbackModels()).containsExactly("fast");}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Test void blocksWhenEveryModelIneligible(){var t=new ModelGatewayService.ModelTarget("bad","https://api.example.com","m",Set.of("embedding"),Set.of("US"),.5,100,new BigDecimal("1"),new BigDecimal("1"),10,false,false);var r=new ModelGatewayService.RouteRequest("R2","x","chat","CN",100,100,new BigDecimal(".01"),true,false,List.of(t));assertThat(service.route(r,"admin").decision()).isEqualTo(ModelGatewayService.Decision.BLOCKED);}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Test void invokesSelectedModelWhenEnabled(){when(client.invoke(any(),any(),any())).thenReturn("answer");var r=new ModelGatewayService.RouteRequest("R3","hello","chat","CN",10,10,new BigDecimal("1"),false,true,List.of(target("deepseek",.9,100,new BigDecimal(".01"),true)));var a=service.route(r,"admin");assertThat(a.output()).isEqualTo("answer");verify(client).invoke(any(),any(),eq("hello"));}
}
