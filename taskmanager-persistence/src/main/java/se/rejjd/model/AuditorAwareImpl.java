package se.rejjd.model;

import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<String> {

	private String auditor;

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

	@Override
	public String getCurrentAuditor() {
		return "DreamierTeam";
	}

}
