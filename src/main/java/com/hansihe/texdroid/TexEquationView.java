package com.hansihe.texdroid;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;

public class TexEquationView extends WebView {

    public static final String dummyHtml = "file:///android_asset/tex_render/dummy.html";

    GestureDetector gestureDetector = null;

    private TexEquationRendered equation = null;

    private float contentScale = 1;

    public TexEquationRendered getEquation() {
        return equation;
    }

    public TexEquationView(Context context) {
        super(context);
        init(context, null);
    }

    public TexEquationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TexEquationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.setBackgroundColor(0x00000000);

        if (attributeSet != null) {
            TypedArray attrs = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.TexEquationView, 0, 0);
            try {
                contentScale = attrs.getFloat(R.styleable.TexEquationView_content_scale, 1);
            } finally {
                attrs.recycle();
            }
        }

        new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent e) {
                Log.d("TexEquationView", "LongPress");
            }
        });
    }

    public void setEquation(TexEquationRendered renderedEquation) {
        this.equation = renderedEquation;
        this.loadDataWithBaseURL(dummyHtml, TexEquationUtil.constructHtmlDocument(equation), "text/html", "utf-8", null);
        this.setBackgroundColor(0x00000000);

        requestLayout();
    }

    public void renderEquation(Context ctx, TexEquation texEquation) {
        ctx.startService(TexRenderService.createTexRenderIntent(ctx, texEquation, new ITexRenderResponseHandler() {
            @Override
            public void result(TexEquationRendered result) {
                setEquation(result);
            }

            @Override
            public void error(String message) {
                loadDataWithBaseURL(dummyHtml, TexEquationUtil.constructErrorDocument(message), "text/html", "utf-8", null);
            }
        }));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        TexEquationRendered equation = getEquation();
        if (equation == null) { // TODO: Add loading bar or something
            setMeasuredDimension(0, 0);
            return;
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int measuredHeight = heightSize;
        int measuredWidth = widthSize;

        int equationHeight = (int) Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, equation.getHeight() * contentScale, getResources().getDisplayMetrics()));
        int equationWidth = (int) Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, equation.getWidth() * contentScale, getResources().getDisplayMetrics()));

        if (widthMode != MeasureSpec.EXACTLY) {
            measuredWidth = equationWidth;

            if (widthMode == MeasureSpec.AT_MOST) {
                if (measuredWidth > widthSize) {
                    measuredWidth = widthSize;
                }
            }
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            measuredHeight = equationHeight;

            if (widthMode != MeasureSpec.UNSPECIFIED) {
                measuredHeight = (int) Math.ceil((float) measuredWidth * ((float) equationHeight / (float) equationWidth));
            }

            if (heightMode == MeasureSpec.AT_MOST) {
                if (measuredHeight > heightSize) {
                    measuredHeight = heightSize;
                }
            }
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
