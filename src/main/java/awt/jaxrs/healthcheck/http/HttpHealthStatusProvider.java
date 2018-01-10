package awt.jaxrs.healthcheck.http;

import java.util.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.typesafe.config.*;

import awt.jaxrs.healthcheck.*;

public class HttpHealthStatusProvider implements HealthStatusProvider {
    private static final String ROOT_ELEMENT = "httpstatus";
    private static final String NAME = "name";
    private static final String URL = "url";
    private static final String METHOD = "method";

    @Override
    public Collection<HealthStatus> getHealthStatuses() {
	final Collection<HealthStatus> statuses = new HashSet<>();
	try {
	    final Config config = ConfigFactory.load().getConfig(ROOT_ELEMENT);
	    final ConfigObject root = config.root();
	    root.entrySet().forEach((entry) -> {
		final String key = entry.getKey();
		final Config child = config.getConfig(key);

		final String name = child.getString(NAME);
		final String url = child.getString(URL);
		final String method = child.getString(METHOD);

		final HealthStatus status = new HealthStatus();
		status.setResource(name);

		final Client client = ClientBuilder.newClient();
		Response response = null;
		try {
		    final Invocation.Builder builder = client.target(url).request();
		    for (final Map.Entry<String, String> header : this.getHeaders().entrySet()) {
			builder.header(header.getKey(), header.getValue());
		    }

		    response = this.getEntity() == null ? builder.method(method)
			    : builder.method(method, this.getEntity());
		    final boolean successful = response.getStatusInfo().getFamily() == Status.Family.SUCCESSFUL;
		    status.setSuccessful(successful);
		    status.setMessage(
			    "Returned " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase());
		} catch (final Exception exception) {
		    status.setSuccessful(false);
		    status.setMessage(exception.getMessage());
		} finally {
		    if (response != null) {
			response.close();
		    }
		}

		statuses.add(status);
	    });
	} catch (final ConfigException.Missing ignore) {
	    // ignore missing configurations.
	}

	return statuses;
    }

    public Entity<?> getEntity() {
	return null;
    }

    public Map<String, String> getHeaders() {
	return Collections.emptyMap();
    }
}
