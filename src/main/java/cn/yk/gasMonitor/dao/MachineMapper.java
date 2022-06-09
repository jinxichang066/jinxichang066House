package cn.yk.gasMonitor.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.yk.gasMonitor.domain.Machine;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Mapper
public interface MachineMapper extends BaseMapper<Machine> {
}
