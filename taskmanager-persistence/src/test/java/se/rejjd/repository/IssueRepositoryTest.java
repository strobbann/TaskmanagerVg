package se.rejjd.repository;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import se.rejjd.AbstractTest;
import se.rejjd.model.Issue;
import se.rejjd.model.WorkItem;

public class IssueRepositoryTest extends AbstractTest {

	@MockBean
	IssueRepository issueRepository;

	@Test
	public void canFindByWorkitemId() {
		WorkItem workItem = new WorkItem("Title", "descrption");
		Issue issue = new Issue(workItem, "title1");
		Issue issue1 = new Issue(workItem, "title2");

		when(issueRepository.findByWorkItemId(1L)).thenReturn(Stream.of(issue, issue1).collect(Collectors.toList()));
		List<Issue> issues = issueRepository.findByWorkItemId(1L).stream().collect(Collectors.toList());
		assertThat(issues, hasItems(issue, issue1));
	}

}
