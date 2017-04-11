package se.rejjd.service;

public final class ServiceException extends Exception {

	private static final long serialVersionUID = 673945921543649662L;

	ServiceException(String message) {
		super(message);
	}

	public ServiceException(String string, Throwable e) {
		super(string, e);
	}

}
