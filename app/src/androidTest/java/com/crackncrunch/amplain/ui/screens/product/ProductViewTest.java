package com.crackncrunch.amplain.ui.screens.product;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.TextView;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.ui.activities.RootActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Lilian on 21-Mar-17.
 */
public class ProductViewTest {

    public ActivityTestRule<RootActivity> mActivityTestRule = new
            ActivityTestRule<>(RootActivity.class);


    private ViewInteraction mPlusBtn;
    private ViewInteraction mMinusBtn;
    private ViewInteraction mProductCountTxt;

    @Before
    public void setup() {
        mActivityTestRule.launchActivity(null);
        mPlusBtn = onView(allOf(withId(R.id.plus_btn), isCompletelyDisplayed()));
        mMinusBtn = onView(allOf(withId(R.id.minus_btn), isCompletelyDisplayed()));
        mProductCountTxt = onView(allOf(withId(R.id.product_count_txt),
                isCompletelyDisplayed()));
    }

    String getText(final Matcher<View> matcher) {
        final String[] stringHolder = { null };
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView)view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }

    @Test
    public void clickPlus() throws Exception {
        //mShowCatalogBtn.perform(click());
        //String count = getText(mProductCountTxt);
        mPlusBtn.perform(click());
    }

    @Test
    public void clickMinus() throws Exception {

    }
}