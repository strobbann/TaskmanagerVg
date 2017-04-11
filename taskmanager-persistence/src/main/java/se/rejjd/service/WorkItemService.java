package se.rejjd.service;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import se.rejjd.model.Issue;
import se.rejjd.model.User;
import se.rejjd.model.WorkItem;
import se.rejjd.model.WorkItem.Status;
import se.rejjd.repository.IssueRepository;
import se.rejjd.repository.WorkItemRepository;

@Component
public final class WorkItemService {

	private final WorkItemRepository workItemRepository;
	private final UserService userService;
	private final IssueRepository issueRepository;
	private final ServiceTransaction transaction;

	@Autowired
	public WorkItemService(WorkItemRepository workItemRepository, UserService userService,
			IssueRepository issueRepository, ServiceTransaction transaction) {
		this.workItemRepository = workItemRepository;
		this.userService = userService;
		this.issueRepository = issueRepository;
		this.transaction = transaction;
	}

	public WorkItem addOrUpdateWorkItem(WorkItem workItem) throws ServiceException {
		return transaction.executeAction(()-> {
			return workItemRepository.save(workItem);
		});
	}

	public WorkItem updateWorkItemStatus(WorkItem workItem, WorkItem.Status status) throws ServiceException {
		try {
			if (status == Status.ARCHIVED) {
				return transaction.execute(() -> {
					Collection<Issue> issues = issueRepository.findByWorkItemId(workItem.getId());
					issues.forEach(i -> i.setOpenIssue(false));

					issueRepository.save(issues);
					workItem.setStatus(status);

					return workItemRepository.save(workItem);
				});

			} else if (status == Status.DONE) {
				Collection<Issue> issues = issueRepository.findByWorkItemId(workItem.getId());
				for (Issue issue : issues) {
					if (issue.isOpenIssue()) {
						throw new ServiceException("work item still has open issues");
					}
				}
				workItem.setStatus(status);
				workItem.setDateOfCompletion(getCurrentDate());
			} else {
				workItem.setDateOfCompletion("");
				workItem.setStatus(status);
			}
			return addOrUpdateWorkItem(workItem);
		} catch (DataAccessException e) {
			throw new ServiceException("Could not update WorkItem status", e);
		}
	}

	public void addUserToWorkItem(WorkItem workItem, User user) throws ServiceException {
		User userToDB = userService.addOrUpdateUser(user);
		if (userToDB.isActiveUser() && isValidAmountOfWorkItems(userToDB)) {
			workItem.setUser(userToDB);
			addOrUpdateWorkItem(workItem);
		} else {
			throw new ServiceException("user must be active and cannot have more than 5 work items");
		}
	}

	public WorkItem getWorkItemById(Long id) {
		return workItemRepository.findOne(id);
	}

	public Collection<WorkItem> getWorkItemsByStatus(WorkItem.Status status) {
		return workItemRepository.findByStatus(status);
	}

	public Collection<WorkItem> getAllWorkItemsByTeam(Long teamId) {
		return workItemRepository.getWorkItemsByTeamId(teamId);
	}

	public Collection<WorkItem> getAllWorkItemsByUser(User user) {
		return workItemRepository.findByUserId(user.getId());
	}

	public Collection<WorkItem> getAllWorkItemsWithIssues() {
		return workItemRepository.getAllWorkItemsWithIssues();
	}

	public Collection<WorkItem> getWorkItemByDescripton(String description) {
		return workItemRepository.findByDescription(description);
	}

	public Collection<WorkItem> getAllWorkItems(int page, int pageSize) {
		Pageable pageRequest = pageRequest(page, pageSize);
		Page<WorkItem> pageFromDb = workItemRepository.findAll(pageRequest);

		return pageFromDb.getContent();
	}

	public Collection<WorkItem> getWorkItemsBetweenDates(String startDate, String endDate) {
		return workItemRepository.findByDateOfCompletionBetween(startDate, endDate);
	}

	private boolean isValidAmountOfWorkItems(User user) {
		return workItemRepository.countByUserId(user.getId()) < 5;
	}

	private String getCurrentDate() {
		return LocalDate.now().toString();
	}

	private Pageable pageRequest(int page, int pageSize) {
		return new PageRequest(page, pageSize);
	}

}
