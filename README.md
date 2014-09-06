TexDroid
========

Android library for rendering and displaying TeX markup in UI. It uses MathJax in a WebView running in a Service to render the TeX markup. Rendered markup is returned in the form of a fully self contained SVG file, in addition to the default pixel size of the equation. The data returned from the service can easily be passed into a TexEquationView to display, or used for other things.

The TexEquationView is backed by a WebView (javascript disabled for performance) with custom measuring code. When set to wrap_content, the TexEquationView will automatically display the equation in a reasonable size. If the size is set manually, the equation will automatically scale to fill the availible space.
