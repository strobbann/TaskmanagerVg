package se.rejjd.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import se.rejjd.AbstractTest;
import se.rejjd.model.WorkItem;

public final class WorkItemRepositoryTest extends AbstractTest {

	@MockBean
	WorkItemRepository workItemRepository;

	private String title;
	private String description;
	WorkItem workItem = new WorkItem(title, description);

	@Test
	public void canFindWorkItemByStatus() {
		WorkItem.Status status = workItem.getStatus();
		when(workItemRepository.findByStatus(status)).thenReturn(Stream.of(workItem).collect(Collectors.toList()));
		List<WorkItem> workItems = workItemRepository.findByStatus(status).stream().collect(Collectors.toList());
		WorkItem workItemfromDb = workItems.get(0);
		assertThat(workItemfromDb, is(workItemfromDb));
	}

	@Test
	public void canFindWorkitemByTeamId() {
		when(workItemRepository.findByDescription(description))
				.thenReturn(Stream.of(workItem).collect(Collectors.toList()));
		List<WorkItem> workItems = workItemRepository.findByDescription(description).stream()
				.collect(Collectors.toList());
		WorkItem workItemfromDb = workItems.get(0);
		assertThat(workItemfromDb, is(workItem));

	}
}
