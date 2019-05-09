package com.mhkj.filter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public class TraceServletRequestWrapper extends HttpServletRequestWrapper {

    TraceServletInputStream traceInputStream;

    public TraceServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (traceInputStream == null) {
            traceInputStream = new TraceServletInputStream(super.getInputStream());
        }
        return traceInputStream;
    }

    public TraceServletInputStream getTraceInputStream() {
        return traceInputStream;
    }

}
