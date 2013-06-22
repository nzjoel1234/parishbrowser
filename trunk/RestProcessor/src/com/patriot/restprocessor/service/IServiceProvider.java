package com.patriot.restprocessor.service;

import android.os.Bundle;

/**
 * Implementations of this interface should allow methods on their respective Processor to be called through the RunTask method.
 */
public interface IServiceProvider
{
	/**
	 * A common interface for all Processors.
	 * This method should make a call to the processor and return the result.
	 * @param methodId The method to call on the processor.
	 * @param extras   Parameters to pass to the processor.
	 * @return         The result of the method
	 */
	boolean RunTask(int methodId, Bundle extras);
}
