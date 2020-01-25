package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.mapper.MessageMapper;
import com.joininterngroup.joinintern.model.Message;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    private MessageMapper messageMapper;

    public MessageController(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/get")
    public List<Message> getMessage(
            @RequestParam String user_id
    ) {
        return this.messageMapper.select(c -> c);
    }
}
