package com.patriot.restprocessor.service;

import android.os.Bundle;

public interface IProcessorResultReceiver
{
    void onReceiveProccesorResult(int methodId, Bundle extras, boolean result);
}
