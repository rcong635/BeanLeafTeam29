package com.example.beanleafteam29;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagKey;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class MapsActivityTest {
    @Rule
    public ActivityTestRule<MapsActivity> mMapActivityTestRule = new ActivityTestRule<>(MapsActivity.class);


    String usernameAdmin = new String("testadmin@gmail.com");
    String passwordAdmin = new String("12345678");

    String usernameCustomer = new String("testcustomer@gmail.com");
    String passwordCustomer = new String("12345678");

    String usernameNew = new String("newuser@gmail.com");
    String passwordNew = new String("12345678");
    String userFullNameNew = new String("new user");


    @Before
    public void setUp() throws Exception {
        Intents.init();
        loginAdmin();

    }



    @Test
    public void test1_LoginAdmin() throws InterruptedException {
        Thread.sleep(2000);

        logoutBlackBox();
        onView(withId(R.id.map)).check(doesNotExist());
        Thread.sleep(3000);
        loginAdmin();
    }

    @Test
    public void test2_LoginCustomer() throws InterruptedException {
        Thread.sleep(2000);

        logoutBlackBox();

        onView(withId(R.id.map)).check(doesNotExist());
        Thread.sleep(3000);
        loginCustomer();
    }

//    @Test
//    public void test0_Login() throws InterruptedException {
//        try {
//            onView(withId(R.id.map_menu)).perform(click());
//        }
//        catch (Exception e){
//           loginAdmin();
//            onView(withId(R.id.map_menu)).perform(click());
//        }
//
//    }

    @Test
    public void test3_openUserMenu() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.map_menu)).perform(click());
//        try {
//            onView(withId(R.id.map_menu)).perform(click());
//        }
//        catch (Exception e){
//            onView(withText("Sign in with email")).perform(click());
//            onView(withHint("Email")).perform(typeText("anyemail@gmail.com"));
//            onView(withText("Next")).perform(click());
//            onView(withHint("Password")).perform(typeText("12345678"));
//            onView(withText("SIGN IN")).perform(click());
//            Thread.sleep(2000);
//            onView(withId(R.id.map_menu)).perform(click());
//        }

    }

    @Test
    public void test4_GoToProfilePage() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.map_menu)).perform(click());
        onView(withText("Profile")).perform(click());
        intended(hasComponent(ProfileActivity.class.getName()));

    }

    @Test
    public void test5_GoToHistoryPage() throws InterruptedException {
        Thread.sleep(3000);
        onView(withId(R.id.map_menu)).perform(click());
        onView(withText("View History")).perform(click());
        intended(hasComponent(UserHistoryActivity.class.getName()));

    }


    @Test
    public void test6_NoAddLocationForCustomer() throws InterruptedException {

        //logout if looged in
        FirebaseUIActivity.logout(mMapActivityTestRule.getActivity());
        Thread.sleep(3000);
        //make sure customer is logged in
        loginCustomer();
        Thread.sleep(3000);

        onView(withId(R.id.map_menu)).perform(click());
        Espresso.onView(withText("Add Location")).check(doesNotExist());

    }


    @Test
    public void test7_GoToAddLocationAdmin() throws InterruptedException {
        try { //if the user is an admin

            FirebaseUIActivity.logout(mMapActivityTestRule.getActivity());
            Thread.sleep(3000);
            loginAdmin();
            Thread.sleep(3000);


            onView(withId(R.id.map_menu)).perform(click());
            Espresso.onView(withText("Add Location")).check(matches(isDisplayed()));
            onView(withText("Add Location")).perform(click());
            intended(hasComponent(AddLocActivity.class.getName()));

        } catch (NoMatchingViewException e) { // if user is not an admin
            Espresso.onView(withText("Add Location")).check(doesNotExist());

        }

    }



    @Test
    public void test8_LogOut() throws InterruptedException {
        Thread.sleep(3000);
        logoutBlackBox();

    }

    @Test
    public void test9_SignUp() throws InterruptedException {
        Thread.sleep(3000);
        logoutBlackBox();
        Thread.sleep(3000);

        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText(usernameNew));
        onView(withText("Next")).perform(click());
        onView(withHint("First & last name")).perform(typeText(userFullNameNew));
        onView(withHint("Password")).perform(typeText(passwordAdmin));
        Espresso.closeSoftKeyboard();
        onView(withText("SAVE")).perform(click());
        Thread.sleep(5000);
        Espresso.onView(withId(R.id.map)).check(matches(isDisplayed()));




    }


    @After
    public void tearDown() throws Exception {
        Intents.release();
        FirebaseUIActivity.logout(mMapActivityTestRule.getActivity());
    }


    private void loginAdmin(){
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText(usernameAdmin));
        onView(withText("Next")).perform(click());
        onView(withHint("Password")).perform(typeText(passwordAdmin));
        onView(withText("SIGN IN")).perform(click());
    }

    private void loginCustomer(){
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText(usernameCustomer));
        onView(withText("Next")).perform(click());
        onView(withHint("Password")).perform(typeText(passwordCustomer));
        onView(withText("SIGN IN")).perform(click());
    }

    private void logoutBlackBox() {
        onView(withId(R.id.map_menu)).perform(click());
        onView(withText("Logout")).perform(click());
        onView(withId(R.id.map_menu)).check(doesNotExist());
        onView(withId(R.id.map)).check(doesNotExist());
    }
}