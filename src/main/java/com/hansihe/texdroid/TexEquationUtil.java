package com.hansihe.texdroid;

public class TexEquationUtil {

    public static String constructHtmlDocument(TexEquationRendered equation) {
        return "<html><head>" +
                    "<style>body { margin: 0; }</style>" +
                "</head><body>" + equation.getSvg() +
                "</body></html>";
    }

    public static String constructErrorDocument(String message) {
        return "<html><body>X</body></html>";
    }

}
