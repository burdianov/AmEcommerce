package com.crackncrunch.amplain.ui.screens.auth;

import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.ui.activities.SplashActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static org.hamcrest.Matchers.not;

/**
 * Created by Lilian on 12-Mar-17.
 */
public class AuthViewTest {
    @Rule
    public ActivityTestRule<SplashActivity> mActivityActivityTestRule = new
            ActivityTestRule<SplashActivity>(SplashActivity.class);

    private static final String TEST_EMAIL_VALID = "anymail@mail.ru";
    private static final String TEST_EMAIL_INVALID = "anymail@mail";
    private static final String TEST_HINT_VALID = "Enter email";
    private static final String TEST_HINT_INVALID = "Wrong email format";

    private ViewInteraction mLoginBtn;
    private ViewInteraction mShowCatalogBtn;
    private ViewInteraction mLoginInput;
    private ViewInteraction mLoginWrapper;
    private ViewInteraction mPasswordInput;

    @Before
    public void setup() {
//        mLoginBtn = onView(Matchers.allOf(withId(R.id.login_btn), withText("Login")));
        mLoginBtn = onView(withId(R.id.login_btn));
        mShowCatalogBtn = onView(withId(R.id.show_catalog_btn));
        mLoginInput = onView(withId(R.id.login_email_et));
        mLoginWrapper = onView(withId(R.id.login_email_wrap));
        mPasswordInput = onView(withId(R.id.login_password_et));
    }

    @Test
    public void click_on_login_HIDE_SHOW_CATALOG_BTN() throws Exception {
        mLoginBtn.perform(click());
        mShowCatalogBtn.check(matches(not(isDisplayed())));
    }

    @Test
    public void back_pressed_LOGIN_CARD_NOT_DISPLAYED() throws Exception {
        mLoginBtn.perform(click());
        pressBack();
        mShowCatalogBtn.check(matches(isDisplayed()));
    }

    @Test
    public void input_valid_mail_password_VALID_HINT() throws Exception {
        mLoginBtn.perform(click());
        mLoginInput.perform(typeTextIntoFocusedView(TEST_EMAIL_VALID));
        mLoginInput.check(matches(withText(TEST_EMAIL_VALID)));

        mLoginWrapper.check(matches(withHintTextInputLayout(TEST_HINT_VALID)));

        mLoginInput.perform(pressKey(KEYCODE_ENTER));
        mPasswordInput.check(matches(hasFocus()));

        mPasswordInput.perform(typeTextIntoFocusedView("AnyPassword"));
        mPasswordInput.check(matches(withText("AnyPassword")));
    }

    @Test
    @Ignore
    public void input_invalid_mail_password_INVALID_HINT() throws Exception {
        mLoginBtn.perform(click());
        mLoginInput.perform(typeTextIntoFocusedView(TEST_EMAIL_INVALID));
        mLoginInput.check(matches(withText(TEST_EMAIL_INVALID)));

        mLoginWrapper.check(matches(withHintTextInputLayout
                (TEST_HINT_INVALID)));

        mLoginInput.perform(pressKey(KEYCODE_ENTER));
        mPasswordInput.check(matches(hasFocus()));

        mPasswordInput.perform(typeTextIntoFocusedView("AnyPassword"));
        mPasswordInput.check(matches(withText("AnyPassword")));
    }

    public static Matcher<View> withHintTextInputLayout(final String
                                                             expectedHint) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof TextInputLayout)) {
                    return false;
                }
                String hint = ((TextInputLayout) item).getHint().toString();
                return expectedHint.equals(hint);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}