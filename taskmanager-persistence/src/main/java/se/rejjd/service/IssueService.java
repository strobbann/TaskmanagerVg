package se.rejjd.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import se.rejjd.model.Issue;
import se.rejjd.model.WorkItem;
import se.rejjd.model.WorkItem.Status;
import se.rejjd.repository.IssueRepository;
import se.rejjd.repository.WorkItemRepository;

@Component
public final class IssueService {

	private final IssueRepository issuerepository;
	private final WorkItemRepository workItemRepository;
	private final ServiceTransaction transaction;

	@Autowired
	public IssueService(IssueRepository issueRepository, WorkItemRepository workItemRepository,
			ServiceTransaction transaction) {
		this.issuerepository = issueRepository;
		this.workItemRepository = workItemRepository;
		this.transaction = transaction;
	}

	public Issue addIssue(WorkItem workItem, String description) throws ServiceException {
		try {
			if (workItem.getStatus() == Status.DONE) {
				return transaction.execute(() -> {
					workItem.setStatus(Status.UNSTARTED);
					workItemRepository.save(workItem);
					return addOrUpdate(new Issue(workItem, description));
				});
			} else {
				throw new ServiceException("Invalid work item status");
			}
		} catch (DataAccessException e) {
			throw new ServiceException("Could not add Issue", e);
		}
	}

	private Issue addOrUpdate(Issue issue) throws ServiceException {
		return transaction.executeAction(() -> {
			return issuerepository.save(issue);
		});
	}

	public Issue updateIssue(Issue issue) throws ServiceException {
		Issue issueDb = issuerepository.findOne(issue.getId());
		if (issueDb == null) {
			throw new ServiceException("Issue not found!");
		}
		return addOrUpdate(issue);
	}

	public Collection<Issue> getAllIssues() {
		List<Issue> listOfIssues = new ArrayList<>();
		issuerepository.findAll().forEach(i -> listOfIssues.add(i));
		return listOfIssues;
	}

	public Issue findIssueById(Long id) {
		return issuerepository.findOne(id);
	}
}
