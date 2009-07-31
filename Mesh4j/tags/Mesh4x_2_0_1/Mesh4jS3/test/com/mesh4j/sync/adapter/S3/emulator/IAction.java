package com.mesh4j.sync.adapter.S3.emulator;

public interface IAction<T> {

	void execute(T target);
}
