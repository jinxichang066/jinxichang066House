package cn.yk.gasMonitor.controller;

import cn.yk.gasMonitor.auth.Auth;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.dto.HistorySearchDTO;
import cn.yk.gasMonitor.service.GasSpecService;
import cn.yk.gasMonitor.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    @Auth
    public PageResult search(HttpServletRequest request, @RequestBody HistorySearchDTO historySearchDTO) {
        return historyService.search(historySearchDTO);

    }

    @RequestMapping("/getImage")
    public void getImage(@RequestParam String machineUrl, @RequestParam String warningInfoId, @RequestParam String mode, HttpServletResponse response) {
        historyService.getImage(machineUrl, warningInfoId, mode, response);

    }

    @RequestMapping("/getGasNameList")
    @Auth
    public PageResult getGasNameList(HttpServletRequest request) {
        return gasSpecService.getGasNameList();

    }

    @RequestMapping("/assembleWord")
    public void assembleWord(HttpServletResponse response, @RequestBody HistorySearchDTO historySearchDTO) throws IOException, InvalidFormatException {
        historyService.assembleWord(response, historySearchDTO);
    }

}
