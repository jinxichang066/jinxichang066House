package cn.yk.gasMonitor.dao;

import cn.yk.gasMonitor.domain.Token;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Mapper
public interface TokenMapper extends BaseMapper<Token> {
}
