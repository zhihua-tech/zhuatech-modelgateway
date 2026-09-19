/* Copyright © 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.modelgateway.service;
import cn.zhuatech.modelgateway.model.GatewayAudit;import cn.zhuatech.modelgateway.repository.GatewayAuditRepository;import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.math.*;import java.nio.charset.StandardCharsets;import java.security.MessageDigest;import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service public class ModelGatewayService{
 private final GatewayAuditRepository repository;private final OpenAiCompatibleClient client;/**
                                                                                              * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                              */
public ModelGatewayService(GatewayAuditRepository r,OpenAiCompatibleClient c){repository=r;client=c;}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Transactional public RouteResult route(RouteRequest r,String actor){
  var old=repository.findByRequestId(r.requestId());if(old.isPresent()){var e=old.get();return new RouteResult(e.getId(),Decision.valueOf(e.getDecision()),e.getModel(),List.of(),BigDecimal.ZERO,"",List.of("重复请求已返回原审计结果"),true);}
  List<String>reasons=new ArrayList<>();List<Scored>eligible=new ArrayList<>();
  for(ModelTarget t:r.targets()){BigDecimal cost=t.inputCostPer1k().multiply(BigDecimal.valueOf(r.estimatedInputTokens())).add(t.outputCostPer1k().multiply(BigDecimal.valueOf(r.estimatedOutputTokens()))).divide(BigDecimal.valueOf(1000),6,RoundingMode.HALF_UP);List<String>miss=new ArrayList<>();if(!t.healthy())miss.add("不健康");if(!t.capabilities().contains(r.requiredCapability()))miss.add("缺少能力");if(!t.regions().contains(r.requiredRegion()))miss.add("地域不符");if(t.remainingTokens()<r.estimatedInputTokens()+r.estimatedOutputTokens())miss.add("配额不足");if(!t.rateLimitAvailable())miss.add("触发限流");if(cost.compareTo(r.maxCost())>0)miss.add("超预算");if(miss.isEmpty()){double score=t.qualityScore()*100-t.p95LatencyMs()/100.0-cost.doubleValue()*10;eligible.add(new Scored(t,cost,score));}else reasons.add(t.name()+"："+String.join("/",miss));}
  eligible.sort(Comparator.comparingDouble(Scored::score).reversed());if(eligible.isEmpty()){var saved=repository.save(new GatewayAudit(r.requestId(),"NONE",Decision.BLOCKED.name(),hash(r.prompt()),"无可用模型；"+String.join("；",reasons),actor));return new RouteResult(saved.getId(),Decision.BLOCKED,"",List.of(),BigDecimal.ZERO,"",List.copyOf(reasons),false);}
  Scored chosen=eligible.getFirst();List<String>fallbacks=r.allowFallback()?eligible.stream().skip(1).limit(3).map(x->x.target().name()).toList():List.of();String output=r.execute()?client.invoke(chosen.target().baseUrl(),chosen.target().model(),r.prompt()):"";
  var saved=repository.save(new GatewayAudit(r.requestId(),chosen.target().name(),Decision.ROUTED.name(),hash(r.prompt()),"cost="+chosen.cost()+", fallback="+fallbacks.size()+", executed="+r.execute(),actor));
  return new RouteResult(saved.getId(),Decision.ROUTED,chosen.target().name(),fallbacks,chosen.cost(),output,List.copyOf(reasons),false);
 }
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private String hash(String value){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));}catch(Exception e){throw new IllegalStateException(e);}}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Transactional(readOnly=true)public List<GatewayAudit>audits(){return repository.findTop100ByOrderByCreatedAtDesc();}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record ModelTarget(@NotBlank String name,@NotBlank String baseUrl,@NotBlank String model,@NotEmpty Set<String>capabilities,@NotEmpty Set<String>regions,@DecimalMin("0")@DecimalMax("1")double qualityScore,@Min(1)long p95LatencyMs,@NotNull@DecimalMin("0")BigDecimal inputCostPer1k,@NotNull@DecimalMin("0")BigDecimal outputCostPer1k,@Min(0)long remainingTokens,boolean healthy,boolean rateLimitAvailable){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record RouteRequest(@NotBlank String requestId,@NotBlank String prompt,@NotBlank String requiredCapability,@NotBlank String requiredRegion,@Min(1)int estimatedInputTokens,@Min(0)int estimatedOutputTokens,@NotNull@DecimalMin("0")BigDecimal maxCost,boolean allowFallback,boolean execute,@NotEmpty List<@Valid ModelTarget>targets){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private record Scored(ModelTarget target,BigDecimal cost,double score){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record RouteResult(Long auditId,Decision decision,String selectedModel,List<String>fallbackModels,BigDecimal estimatedCost,String output,List<String>excludedReasons,boolean duplicate){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public enum Decision{ROUTED,BLOCKED}
}
