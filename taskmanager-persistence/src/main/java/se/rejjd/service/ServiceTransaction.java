package se.rejjd.service;

import javax.transaction.Transactional;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import se.rejjd.model.AbstractEntity;

@Component
class ServiceTransaction {

	@Transactional
	public <E extends AbstractEntity> E execute(Action<E> action) throws ServiceException {
		try {
			return action.action();
		} catch (DataAccessException e) {
			throw new ServiceException("Requested action could not be performed!");
		}
	}
	
	public <E extends AbstractEntity> E executeAction(Action<E> action) throws ServiceException{
		try {
			return action.action();
		} catch (DataIntegrityViolationException e) {
			throw new ServiceException("Invalid Parameter Value: " + e.getRootCause().getMessage());
		}
	}

	@FunctionalInterface
	public static interface Action<E extends AbstractEntity> {
		E action() throws ServiceException;
	}
}
