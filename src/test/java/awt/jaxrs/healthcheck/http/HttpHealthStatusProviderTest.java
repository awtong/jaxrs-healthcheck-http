package awt.jaxrs.healthcheck.http;

import java.util.Collection;

import org.junit.Test;

import awt.jaxrs.healthcheck.HealthStatus;

public class HttpHealthStatusProviderTest {
    @Test
    public void testFoo() {
	final HttpHealthStatusProvider provider = new HttpHealthStatusProvider();

	final Collection<HealthStatus> statuses = provider.getHealthStatuses();
	System.out.println(statuses);
    }
}
