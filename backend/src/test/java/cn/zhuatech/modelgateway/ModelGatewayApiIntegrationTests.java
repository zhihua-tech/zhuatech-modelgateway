/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.modelgateway;
import org.junit.jupiter.api.Test;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.test.context.SpringBootTest;import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;import org.springframework.http.MediaType;import org.springframework.test.web.servlet.MockMvc;import java.nio.charset.StandardCharsets;import java.util.Base64;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@SpringBootTest @AutoConfigureMockMvc class ModelGatewayApiIntegrationTests{
 @Autowired MockMvc mvc;/**
                         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                         */
private String auth(){return "Basic "+Base64.getEncoder().encodeToString("admin:test-admin".getBytes(StandardCharsets.UTF_8));}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Test void operatorCanRouteAndPersistAudit()throws Exception{String body="""
 {"requestId":"API-GW-1","prompt":"总结经营风险","requiredCapability":"chat","requiredRegion":"CN","estimatedInputTokens":300,"estimatedOutputTokens":200,"maxCost":0.10,"allowFallback":true,"execute":false,"targets":[{"name":"model-a","baseUrl":"https://api.example.com","model":"chat-a","capabilities":["chat"],"regions":["CN"],"qualityScore":0.95,"p95LatencyMs":300,"inputCostPer1k":0.01,"outputCostPer1k":0.02,"remainingTokens":100000,"healthy":true,"rateLimitAvailable":true}]}
 """;mvc.perform(post("/api/model-gateway/route").header("Authorization",auth()).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.data.decision").value("ROUTED")).andExpect(jsonPath("$.data.selectedModel").value("model-a")).andExpect(jsonPath("$.data.auditId").isNumber());}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Test void anonymousAuditAccessIsDenied()throws Exception{mvc.perform(get("/api/model-gateway/audits")).andExpect(status().isUnauthorized());}
}
