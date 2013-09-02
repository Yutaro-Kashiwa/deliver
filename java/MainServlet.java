package jp.enpit.cloud.tinychat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;


@WebServlet(value = { "/send" })
public class MainServlet extends WebSocketServlet {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6946416208261279049L;

	/**
	 * スレッドセーフなEchoInboundのSet
	 */
	private static final Set<ChatMessageInbound> inbounds = new CopyOnWriteArraySet<ChatMessageInbound>();

	private static Logger logger;

	public MainServlet() {
		logger = Logger.getLogger(getClass().getName());
	}


	/**
	 * 新規クライアント接続時の処理
	 */
	@Override
	protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {
		return new ChatMessageInbound(this);
	}


	/**
	 * 送受信をお行うためのオブジェクト
	 */
	class ChatMessageInbound extends MessageInbound {
		private WebSocketServlet mainServlet;

		public ChatMessageInbound(MainServlet mainServlet) {
			this.mainServlet = mainServlet;
			log("connect", "");
		}

		/**
		 * 接続時の処理
		 */
		@Override
		protected void onOpen(WsOutbound outbound) {
			log("open", outbound.toString());

			// EchoServletの持つsocketリストに自分自身を保存する
			inbounds.add(this);
		}

		/**
		 * 切断時の処理
		 */
		@Override
		protected void onClose(int status) {
			log("close", String.valueOf(status));
			inbounds.remove(this);
		}

		/**
		 * バイナリメッセージ受信時の処理
		 */
		@Override
		protected void onBinaryMessage(ByteBuffer message) throws IOException {
			// 何もしない
		}

		/**
		 * テキストメッセージ受信時の処理
		 */
		@Override
		protected void onTextMessage(CharBuffer buffer) throws IOException {
			log("send", buffer.toString());
			sendMessages(buffer);
		}

		private void log(String type, String message) {
			logger.info("[" + type + "] (" + mainServlet.getServletContext().getContextPath() + ") " + message);
		}

		/**
		 * 全クライアントにメッセージを送信する
		 * @param msgObject
		 * @throws IOException
		 */
		private void sendMessages(CharBuffer buffer) throws IOException{
			try {
				for (MessageInbound socket : inbounds) {
					socket.getWsOutbound().writeTextMessage(buffer);
					buffer.position(0);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}