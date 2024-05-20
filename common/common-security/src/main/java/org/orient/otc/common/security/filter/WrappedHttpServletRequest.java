package org.orient.otc.common.security.filter;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author dzrh
 */
public class WrappedHttpServletRequest extends HttpServletRequestWrapper {
	private final byte[] bytes;

	public WrappedHttpServletRequest(HttpServletRequest request) throws IOException {
		super(request);

		// 读取输入流里的请求参数，并保存到bytes里
		bytes = IOUtils.toByteArray(request.getInputStream());
	}

	@Override
	public ServletInputStream getInputStream() {
		return new BufferedServletInputStream(this.bytes);
	}

	static class BufferedServletInputStream extends ServletInputStream {
		private final ByteArrayInputStream inputStream;
		public BufferedServletInputStream(byte[] buffer) {
			//此处即赋能，可以详细查看ByteArrayInputStream的该构造函数；
			this.inputStream = new ByteArrayInputStream( buffer );
		}
		@Override
		public int available() {
			return inputStream.available();
		}
		@Override
		public int read() {
			return inputStream.read();
		}
		@Override
		public int read(byte[] b, int off, int len) {
			return inputStream.read( b, off, len );
		}
		@Override
		public boolean isFinished() {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean isReady() {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public void setReadListener(ReadListener listener) {
			// TODO Auto-generated method stub

		}
	}
}
