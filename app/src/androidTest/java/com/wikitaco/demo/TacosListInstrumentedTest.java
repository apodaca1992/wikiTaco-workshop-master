package com.wikitaco.demo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.wikitaco.demo.tacolist.TacosListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TacosListInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.wikitaco.demo", appContext.getPackageName());
    }
    @Rule
    public ActivityTestRule<TacosListActivity> mActivityTestRule = new ActivityTestRule<TacosListActivity>(
            TacosListActivity.class, true, true
    ){};

    @Test
    public void validateRecyclerViewClickSecondElement(){
        onView(withId(R.id.rvTacos)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
    }
}
