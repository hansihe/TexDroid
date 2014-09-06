package com.hansihe.texdroid;

public interface ITexRenderResponseHandler {
    public void result(TexEquationRendered result);
    public void error(String message);
}
