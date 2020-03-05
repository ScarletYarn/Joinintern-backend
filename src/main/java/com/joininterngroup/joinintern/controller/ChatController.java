package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.helpers.GlobalMessage;
import com.joininterngroup.joinintern.helpers.UserEssential;
import com.joininterngroup.joinintern.mapper.MessageDynamicSqlSupport;
import com.joininterngroup.joinintern.mapper.MessageMapper;
import com.joininterngroup.joinintern.model.Message;
import com.joininterngroup.joinintern.utils.FileService;
import com.joininterngroup.joinintern.utils.JoinInternEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/chatting")
@ServerEndpoint("/chat")
public class ChatController {

    private static int onlineCount = 0;

    private static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    private String uid;

    private UserController userController;

    private MessageMapper messageMapper;

    private FileService fileService;

    private JoinInternEnvironment joinInternEnvironment;

    @Value("${joinintern.maxChatBufferLen}")
    private Integer maxChatBufferLen;

    public ChatController(UserController userController, MessageMapper messageMapper, FileService fileService, JoinInternEnvironment joinInternEnvironment) {
        this.userController = userController;
        this.messageMapper = messageMapper;
        this.fileService = fileService;
        this.joinInternEnvironment = joinInternEnvironment;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) {
        sessionMap.put(uid, session);
        this.uid = uid;
        addCount();

        UserEssential userEssential = this.userController.query(uid);
        GlobalMessage globalMessage = new GlobalMessage();
        globalMessage.setType("login");
        globalMessage.setUserEssential(userEssential);
        globalMessage.setOnlineCount(onlineCount);

        pushMessage(globalMessage);
    }

    @OnMessage
    public void onMessage(
            Session session,
            @PathParam("receiver") String receiver,
            @PathParam("image") String url,
            String content
    ) {
        String[] strings = url.split("/");
        String bin = null;
        String dir = "media/";
        if (!this.joinInternEnvironment.isProd()) dir += "dev";
        dir += "chat/";
        if (!url.equals("")) {
            bin = this.fileService.getFile(url, String.format("%s/%s", dir, this.uid), strings[strings.length - 1]);
        }
        Message message = new Message();
        message.setHasRead(false);
        message.setMessageTime(new Date());
        message.setMessageContent(content);
        message.setSenderId(this.uid);
        message.setReceiverId(receiver);
        message.setBinaryContent(bin);
        this.messageMapper.insert(message);

        Session receiverSession = sessionMap.get(receiver);
        if (receiverSession != null) {
            try {
                receiverSession.getBasicRemote().sendObject(message);
            } catch (EncodeException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/status")
    public boolean status(
            @RequestParam String uid
    ) {
        return sessionMap.containsKey(uid);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/fetch")
    public List<Message> fetch(
            @RequestParam String uid,
            @RequestParam Integer offset
    ) {
        return this.messageMapper.select(c ->
                c.where(MessageDynamicSqlSupport.receiverId, isEqualTo(uid))
                .or(MessageDynamicSqlSupport.senderId, isEqualTo(uid))
                .groupBy(MessageDynamicSqlSupport.messageTime)
                .limit(10)
                .offset(offset)
        );
    }

    @OnClose
    public void onClose() {
        sessionMap.remove(this.uid);
        declineCount();
    }

    @OnError
    public void onError(Session session, Throwable e) {
        e.printStackTrace();
    }

    public synchronized void addCount() {
        onlineCount++;
    }

    public synchronized void declineCount() {
        onlineCount--;
    }

    private static void pushMessage(GlobalMessage globalMessage) {
        sessionMap.forEach((uid, session) -> {
            try {
                session.getBasicRemote().sendObject(globalMessage);
            } catch (EncodeException | IOException e) {
                e.printStackTrace();
            }
        });
    }
}
