package cn.yk.gasMonitor.controller;

import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.dto.HistorySearchDTO;
import cn.yk.gasMonitor.service.GasSpecService;
import cn.yk.gasMonitor.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Jin Xichang
 * @date 2022/5/12
 **/
@CrossOrigin
@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;
    private final GasSpecService gasSpecService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody HistorySearchDTO historySearchDTO) {
        return historyService.search(historySearchDTO);

    }

    @RequestMapping("/getImage")
    public void getImage(@RequestParam String machineUrl, @RequestParam String warningInfoId, @RequestParam String mode, HttpServletResponse response) {
        historyService.getImage(machineUrl, warningInfoId, mode, response);

    }

    @RequestMapping("/getGasNameList")
    public PageResult getGasNameList() {
        return gasSpecService.getGasNameList();

    }

}
