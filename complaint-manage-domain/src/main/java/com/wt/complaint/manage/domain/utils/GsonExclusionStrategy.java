package com.wt.complaint.manage.domain.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * 
 * @author liubin
 *
 */
public class GsonExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(GsonExclude.class) != null;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

}
