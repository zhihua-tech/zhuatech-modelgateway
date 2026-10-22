/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.modelgateway.workbench;
import org.springframework.stereotype.Service;
import java.math.*;
import java.util.*;
import static cn.zhuatech.modelgateway.workbench.WorkbenchInput.*;
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
    /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    public Map<String,Object> execute(Map<String,Object> r){
        validate(r);

        keys(r,"contextWindow","outputReserve","systemTokens","messages");int window=integer(r,"contextWindow",1,10000000),reserve=integer(r,"outputReserve",0,10000000),system=integer(r,"systemTokens",0,10000000);
        long available=(long)window-reserve-system;if(available<0)throw new IllegalArgumentException("系统消息及输出预算超过窗口");
        var messages=rows(r,"messages",0,200);Set<String> ids=new HashSet<>(),selected=new HashSet<>();List<Map<String,Object>> optional=new ArrayList<>();
        for(var m:messages){keys(m,"id","tokens","priority","required");String id=id(m,"id");if(!ids.add(id))throw new IllegalArgumentException("消息标识重复");int tokens=integer(m,"tokens",0,10000000);decimal(m,"priority",0,100);if(flag(m,"required")){available-=tokens;selected.add(id);}else optional.add(m);}
        if(available<0)throw new IllegalArgumentException("必需上下文超过可用预算");
        optional.sort(Comparator.<Map<String,Object>,BigDecimal>comparing(m->decimal(m,"priority",0,100)).reversed().thenComparing(m->id(m,"id")));
        for(var m:optional){int size=integer(m,"tokens",0,10000000);if(size<=available){selected.add(id(m,"id"));available-=size;}}
        List<String> included=new ArrayList<>(),excluded=new ArrayList<>();for(var m:messages)(selected.contains(id(m,"id"))?included:excluded).add(id(m,"id"));
        return Map.of("included",List.copyOf(included),"excluded",List.copyOf(excluded),"remainingTokens",(int)available,"reservedOutputTokens",reserve,"tokenCountsAreEstimates",true);

    }
}
