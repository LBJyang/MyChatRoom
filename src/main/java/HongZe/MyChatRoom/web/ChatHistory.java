package HongZe.MyChatRoom.web;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.stereotype.Component;

@Component
public class ChatHistory {
	final int maxMessage = 100;
	final List<ChatMessage> historyChatMessages = new ArrayList<ChatMessage>(100);
	final Lock writeLock;
	final Lock readLock;

	public ChatHistory() {
		var lock = new ReentrantReadWriteLock();
		this.writeLock = lock.writeLock();
		this.readLock = lock.readLock();
	}

	public List<ChatMessage> getHistoryChatMessages() {
		readLock.lock();
		try {
			return List.copyOf(historyChatMessages);
		} finally {
			// : handle finally clause
			readLock.unlock();
		}
	}

	public void addToHistoryChatMessages(ChatMessage chatMessage) {
		// TODO Auto-generated method stub
		writeLock.lock();
		try {
			historyChatMessages.add(chatMessage);
			if (historyChatMessages.size() > maxMessage) {
				historyChatMessages.remove(0);
			}
		} finally {
			// TODO: handle finally clause
			writeLock.unlock();
		}
	}
}
