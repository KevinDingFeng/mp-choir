package com.shenghesun.util;

import org.apache.http.NameValuePair;

public class NameValue implements NameValuePair{
	private String name;
	private String value;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
