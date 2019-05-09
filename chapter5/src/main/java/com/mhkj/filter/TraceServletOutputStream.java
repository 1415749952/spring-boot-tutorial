package com.mhkj.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;

public class TraceServletOutputStream extends ServletOutputStream {

    private ServletOutputStream outputStream;

    private StringBuilder buffer;

    public TraceServletOutputStream(ServletOutputStream outputStream) {
        this.outputStream = outputStream;
        buffer = new StringBuilder();
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        if(b.length > 0) {
            buffer.append(new String(b, off, len));
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
        if(b.length > 0) {
            buffer.append(new String(b));
        }
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
    }

    public String getContent() {
        return buffer.toString();
    }

}
