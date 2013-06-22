package com.patriot.restprocessor.service;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowActivity;
import com.xtremelabs.robolectric.shadows.ShadowService;

import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ServiceHelperBaseTest
{
    private class MockServiceClass extends ShadowService
    {        
    };
    
    @Test
    public void runMethodCreatesIntentCorrectly() throws Exception
    {
        // Arrange
        Activity activity = new Activity();
        int providerId = 2;
        int methodId = 3;
        String resultAction = "result action";
        
        ServiceHelper serviceHelper = new ServiceHelper(activity.getApplicationContext(), MockServiceClass.class, providerId, resultAction);
        
        // Act
        serviceHelper.RunMethod(methodId);
        
        // Assert
        ShadowActivity shadowActivity = Robolectric.shadowOf(activity);
        
        Intent startedIntent = shadowActivity.getNextStartedService();
        
        Bundle extras = startedIntent.getExtras();

        assertThat("Provider extra is set.", extras.getInt(ProcessorService.Extras.PROVIDER_EXTRA), IsEqual.equalTo(providerId));
        assertThat("Method extra is set.", extras.getInt(ProcessorService.Extras.METHOD_EXTRA), IsEqual.equalTo(methodId));
        assertThat("Result Action extra is set.", extras.getString(ProcessorService.Extras.RESULT_ACTION_EXTRA), IsEqual.equalTo(resultAction));
    }
    
    @Test
    public void runMethodWithParametersAddsBundleToIntent() throws Exception
    {
        // Arrange
        Activity activity = new Activity();
        int providerId = 2;
        int methodId = 3;
        String resultAction = "result action";
        
        Bundle params = new Bundle();
        
        String booleanParamExtra = "booleanParamExtra";
        boolean booleanParam = true;
        params.putBoolean(booleanParamExtra, booleanParam);
        
        String stringParamExtra = "stringParamExtra";
        String stringParam = "stringParamValue";
        params.putString(stringParamExtra, stringParam);
        
        ServiceHelper serviceHelper = new ServiceHelper(activity.getApplicationContext(), MockServiceClass.class, providerId, resultAction);
        
        // Act
        serviceHelper.RunMethod(methodId, params);
        
        // Assert
        ShadowActivity shadowActivity = Robolectric.shadowOf(activity);
        
        Intent startedIntent = shadowActivity.getNextStartedService();
        
        Bundle extras = startedIntent.getExtras();
        
        assertThat("Assert boolean parameter is set.", extras.getBoolean(booleanParamExtra), IsEqual.equalTo(booleanParam));
        assertThat("Assert string parameter is set.", extras.getString(stringParamExtra), IsEqual.equalTo(stringParam));
    }
}
