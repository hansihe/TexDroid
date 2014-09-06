TexDroid
========

Android library for rendering and displaying TeX markup in UI. It uses MathJax in a WebView running in a Service to render the TeX markup. Rendered markup is returned in the form of a fully self contained SVG file, in addition to the default pixel size of the equation. The data returned from the service can easily be passed into a TexEquationView to display, or used for other things.

The TexEquationView is backed by a WebView (javascript disabled for performance) with custom measuring code. When set to wrap\_content, the TexEquationView will automatically display the equation in a reasonable size. If the size is set manually, the equation will automatically scale to fill the availible space. By using the content\_scale attribute, you can multiply the equation size.

### Examples

test_layout.xml
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_height="match_parent"
    android:layout_width="match_parent"
    android:orientation="vertical">
    <com.hansihe.texdroid.TexEquationView
        android:layout_margin="4dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/texView" />
</LinearLayout>
```

```java
public class TexRenderTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        
        final TexEquationView equationView = (TexEquationView) findViewById(R.id.texView);
        equationView.renderEquation(this, new TexEquation("x = \frac{-b \pm \sqrt{b^2 - 4ac}}{2a}"));
    }
}
```
