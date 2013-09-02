package jp.enpit.cloud.tinychat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;


@WebServlet(value = { "/send" })
public class CMainServlet extends WebSocketServlet {
	private static final long serialVersionUID = 6946416208261279049L;
	private static final Set<ChatMessageInbound> inbounds = new CopyOnWriteArraySet<ChatMessageInbound>();

	@Override
	protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {
		return new ChatMessageInbound();
	}

	class ChatMessageInbound extends MessageInbound {
		@Override
		protected void onOpen(WsOutbound outbound) {
			inbounds.add(this);
		}

		@Override
		protected void onClose(int status) {
			inbounds.remove(this);
		}

		@Override
		protected void onBinaryMessage(ByteBuffer message) throws IOException {}

		@Override
		protected void onTextMessage(CharBuffer buffer) throws IOException {
			sendMessages(buffer);
		}

		private void sendMessages(CharBuffer buffer) throws IOException{
			try {
				for (MessageInbound socket : inbounds) {
					socket.getWsOutbound().writeTextMessage(buffer);
					buffer.position(0);
				}
			} catch(IOException e) {}
		}
	}
}