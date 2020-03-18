package com.cesc.mrbd.activity;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.cesc.mrbd.R;

import org.junit.Rule;
import org.junit.Test;

/**
 * Created by Bynry01 on 26-08-2016.
 */

public class LoginActivityInstrumentationTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    @Rule
    LoginActivity mActivity;
    private AutoCompleteTextView username;
    private EditText password;

    public LoginActivityInstrumentationTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        username = (AutoCompleteTextView) mActivity.findViewById(R.id.act_email);
        password = (EditText) mActivity.findViewById(R.id.ed_password);
    }

    @Test
    public void testPreconditions() {
        assertNotNull(username);
        assertNotNull(password);
    }

    @Test
    public void testText() {
        assertEquals("chavanamol76@gmail.com", username.getText());
        assertEquals("12345", password.getText());
    }
}