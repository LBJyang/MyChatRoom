package HongZe.MyChatRoom.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import HongZe.MyChatRoom.entity.User;

public class ChatHandler extends TextWebSocketHandler {
	Logger logger = LoggerFactory.getLogger(getClass());
	private Map<String, WebSocketSession> clients = new ConcurrentHashMap<String, WebSocketSession>();
	@Autowired
	ChatHistory chatHistory;
	@Autowired
	ObjectMapper objectMapper;

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		// TODO Auto-generated method stub
		super.handleMessage(session, message);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		clients.put(session.getId(), session);
		User user = (User) session.getAttributes().get("__user__");
		String name;
		if (user != null) {
			name = user.getName();
		} else {
			name = initGuestName();
		}
		session.getAttributes().put("name", name);
		logger.info("websocket connection established! id = {},name = {}", session.getId(), name);
		// send history messages
		List<ChatMessage> historyChatMessages = chatHistory.getHistoryChatMessages();
		session.sendMessage(toTextMessage(historyChatMessages));
		// send tip message
		var msg = new ChatMessage("SYSTEM MESSAGE", name + " joined the room.");
		chatHistory.addToHistoryChatMessages(msg);
		broadcast(msg);
	}

	public void broadcast(ChatMessage msg) throws IOException {
		// TODO Auto-generated method stub
		TextMessage message = toTextMessage(List.of(msg));
		for (String id : clients.keySet()) {
			WebSocketSession session = clients.get(id);
			session.sendMessage(message);
		}
	}

	private TextMessage toTextMessage(List<ChatMessage> historyChatMessages) throws JsonProcessingException {
		// TODO Auto-generated method stub
		String json = objectMapper.writeValueAsString(historyChatMessages);
		return new TextMessage(json);
	}

	private String initGuestName() {
		// TODO Auto-generated method stub
		return "Guest" + guestNumber.incrementAndGet();
	}

	private AtomicInteger guestNumber = new AtomicInteger();

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// TODO Auto-generated method stub
		clients.remove(session.getId());
		logger.info("websocket connection closed: id = {}, close-status = {}", session.getId(), status);
	}
}
