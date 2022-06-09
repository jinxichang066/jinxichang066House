package cn.yk.gasMonitor.service;

import cn.yk.gasMonitor.common.Result;
import cn.yk.gasMonitor.dao.GasSpecMapper;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.domain.GasSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Jin Xichang
 * @date 2022/5/29
 **/
@Slf4j
@Service
public class GasSpecService {

    @Resource
    private GasSpecMapper gasSpecMapper;

    public Result getGasNameList() {
        Set<String> gasNameSet = new HashSet<>();

        List<GasSpec> gasSpecs = gasSpecMapper.selectList(Wrappers.lambdaQuery(GasSpec.class));
        if (CollectionUtil.isNotEmpty(gasSpecs)) {
            gasNameSet = gasSpecs.stream().map(GasSpec::getGasName).collect(Collectors.toSet());
        }

        return new Result(true, StatusCode.OK, MessageConstant.GAS_SPEC_NAME_LIST_SUCCESS, gasNameSet);
    }

}
