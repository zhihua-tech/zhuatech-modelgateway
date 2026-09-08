/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.modelgateway.service;
import org.springframework.beans.factory.annotation.Value;import org.springframework.http.client.JdkClientHttpRequestFactory;import org.springframework.stereotype.Component;import org.springframework.web.client.RestClient;import java.net.URI;import java.net.http.HttpClient;import java.time.Duration;import java.util.*;
@Component
public class OpenAiCompatibleClient{
 private final String apiKey;private final Set<String>allowedHosts;
 public OpenAiCompatibleClient(@Value("${model.api-key:}")String apiKey,@Value("${model.allowed-hosts:}")String hosts){this.apiKey=apiKey;this.allowedHosts=new HashSet<>(Arrays.asList(hosts.split(",")));}
 public String invoke(String baseUrl,String model,String prompt){
  URI uri=URI.create(baseUrl);if(!"https".equalsIgnoreCase(uri.getScheme())||!allowedHosts.contains(uri.getHost()))throw new IllegalArgumentException("模型端点未命中 HTTPS 主机白名单");
  if(apiKey.isBlank())throw new IllegalStateException("服务端尚未配置 MODEL_API_KEY");
  HttpClient httpClient=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
  JdkClientHttpRequestFactory factory=new JdkClientHttpRequestFactory(httpClient);factory.setReadTimeout(Duration.ofSeconds(30));
  Map<?,?> response=RestClient.builder().requestFactory(factory).baseUrl(baseUrl).defaultHeader("Authorization","Bearer "+apiKey).build().post().uri("/chat/completions").body(Map.of("model",model,"messages",List.of(Map.of("role","user","content",prompt)),"temperature",0)).retrieve().body(Map.class);
  if(response==null)throw new IllegalStateException("模型提供商返回空响应");
  var choices=(List<?>)response.get("choices");if(choices==null||choices.isEmpty())throw new IllegalStateException("模型响应缺少 choices");
  var choice=(Map<?,?>)choices.getFirst();var message=(Map<?,?>)choice.get("message");return Objects.toString(message.get("content"),"");
 }
}
