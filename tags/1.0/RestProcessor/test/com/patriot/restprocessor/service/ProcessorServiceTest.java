package com.patriot.restprocessor.service;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;

import com.xtremelabs.robolectric.RobolectricTestRunner;

class MockProcessorService extends ProcessorService
{
    /**
     * Identifier for each supported provider.
     * Cannot use 0 as Bundle.getInt(key) returns 0 when the key does not exist.
     */
    public static class Providers
    {
        public static final int PARTITIONS_PROVIDER = 1;
        public static final int SIGNALS_PROVIDER = 2;
    }

    @Override
    protected IServiceProvider GetProvider(int providerId)
    {
//        switch(providerId)
//        {
//        case Providers.PARTITIONS_PROVIDER:
//            return new PartitionsServiceProvider(this);
//        case Providers.SIGNALS_PROVIDER:
//            return new SignalsServiceProvider(this);
//        }
        return null;
    }
}

@RunWith(RobolectricTestRunner.class)
public class ProcessorServiceTest
{

    
    @Test
    public void runMethodCreatesIntentCorrectly() throws Exception
    {
        // Arrange
        MockProcessorService underTest = new MockProcessorService();
        
        Intent intent = new Intent();
        
        underTest.onStartCommand(intent, 0, 1);
    }
}
