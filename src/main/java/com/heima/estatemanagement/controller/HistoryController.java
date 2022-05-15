package com.heima.estatemanagement.controller;

import com.heima.estatemanagement.common.PageResult;
import com.heima.estatemanagement.dto.HistorySearchDTO;
import com.heima.estatemanagement.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/search")
    public PageResult search(@RequestBody HistorySearchDTO historySearchDTO) {
        return historyService.search(historySearchDTO);

    }

}
