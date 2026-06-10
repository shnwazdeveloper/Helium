package dev.shnwaz.helium;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView.OnEditorActionListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String HOME_URL = "app://home";

    private WebView webView;
    private EditText addressBar;
    private View homeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        root.addView(createToolbar());

        homeView = createHomeView();
        root.addView(homeView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        webView = new WebView(this);
        webView.setVisibility(View.GONE);
        configureWebView(webView);
        root.addView(webView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);
        showHome();
    }

    private LinearLayout createToolbar() {
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setPadding(dp(8), dp(8), dp(8), dp(8));
        toolbar.setBackgroundColor(Color.WHITE);

        Button back = toolbarButton("<");
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    showHome();
                }
            }
        });
        toolbar.addView(back);

        Button forward = toolbarButton(">");
        forward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.getVisibility() == View.VISIBLE && webView.canGoForward()) {
                    webView.goForward();
                }
            }
        });
        toolbar.addView(forward);

        Button refresh = toolbarButton("R");
        refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.getVisibility() == View.VISIBLE) {
                    webView.reload();
                }
            }
        });
        toolbar.addView(refresh);

        addressBar = new EditText(this);
        addressBar.setSingleLine(true);
        addressBar.setTextSize(14f);
        addressBar.setHint("Search or enter URL");
        addressBar.setSelectAllOnFocus(true);
        addressBar.setImeOptions(EditorInfo.IME_ACTION_GO);
        addressBar.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        addressBar.setBackgroundColor(Color.rgb(241, 245, 249));
        addressBar.setPadding(dp(12), 0, dp(12), 0);
        addressBar.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                boolean enter = event != null
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_UP;
                if (actionId == EditorInfo.IME_ACTION_GO || enter) {
                    loadFromAddressBar();
                    return true;
                }
                return false;
            }
        });
        toolbar.addView(addressBar, new LinearLayout.LayoutParams(0, dp(44), 1f));

        Button go = toolbarButton("Go");
        go.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFromAddressBar();
            }
        });
        toolbar.addView(go);

        Button home = toolbarButton("Home");
        home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showHome();
            }
        });
        toolbar.addView(home);

        return toolbar;
    }

    private Button toolbarButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(12f);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setPadding(dp(8), 0, dp(8), 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, dp(44));
        params.setMargins(0, 0, dp(6), 0);
        button.setLayoutParams(params);
        return button;
    }

    private View createHomeView() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(24), dp(20), dp(28));
        scrollView.addView(content);

        TextView title = new TextView(this);
        title.setText("SHNWAZ Helium");
        title.setTextSize(28f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.rgb(15, 23, 42));
        content.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Mobile browser by SHNWAZ Developer");
        subtitle.setTextSize(15f);
        subtitle.setTextColor(Color.rgb(71, 85, 105));
        subtitle.setPadding(0, dp(4), 0, dp(18));
        content.addView(subtitle);

        addQuickLink(content, "GitHub", "https://github.com/shnwazdeveloper");
        addQuickLink(content, "Telegram", "https://t.me/Syntaxpy");
        addQuickLink(content, "X", "https://x.com/shnwazdev");
        addQuickLink(content, "LinkedIn", "https://www.linkedin.com/in/shnwazdev/");
        addQuickLink(content, "Instagram", "https://instagram.com/sexyshnwaz");
        addQuickLink(content, "Happenstance", "https://happenstance.ai/u/shnwaz");
        addQuickLink(content, "SayaProject", "https://github.com/SayaProject");
        addQuickLink(content, "SayaGram", "https://github.com/SayaGram");
        addQuickLink(content, "Saya Telegram", "https://t.me/SayaProject");

        return scrollView;
    }

    private void addQuickLink(LinearLayout content, String label, String url) {
        Button link = new Button(this);
        link.setAllCaps(false);
        link.setGravity(Gravity.CENTER_VERTICAL);
        link.setText(label + "  " + url);
        link.setTextSize(14f);
        link.setTextColor(Color.rgb(15, 23, 42));
        link.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrl(url);
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(48));
        params.setMargins(0, 0, 0, dp(8));
        content.addView(link, params);
    }

    private void configureWebView(WebView view) {
        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        view.setWebChromeClient(new WebChromeClient());
        view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!HOME_URL.equals(url)) {
                    addressBar.setText(url);
                }
            }
        });
    }

    private void loadFromAddressBar() {
        String input = addressBar.getText().toString().trim();
        if (input.length() == 0) {
            showHome();
            return;
        }
        loadUrl(normalizeInput(input));
    }

    private void loadUrl(String url) {
        homeView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        addressBar.setText(url);
        webView.loadUrl(url);
    }

    private void showHome() {
        webView.setVisibility(View.GONE);
        homeView.setVisibility(View.VISIBLE);
        addressBar.setText("");
        addressBar.setHint("Search or enter URL");
    }

    private String normalizeInput(String input) {
        String lower = input.toLowerCase(Locale.US);
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            return input;
        }
        if (input.contains(".") && !input.contains(" ")) {
            return "https://" + input;
        }
        try {
            return "https://www.google.com/search?q="
                    + URLEncoder.encode(input, "UTF-8");
        } catch (Exception ignored) {
            return "https://www.google.com/search?q=" + Uri.encode(input);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onBackPressed() {
        if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
            webView.goBack();
        } else if (webView.getVisibility() == View.VISIBLE) {
            showHome();
        } else {
            super.onBackPressed();
        }
    }
}
