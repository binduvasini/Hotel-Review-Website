package cs601.project;

/**
 * @author vasini
 *
 *         abstract class for handling the HTTP requests
 */
public abstract class HTTPHandler {
	private Object resource;

	public abstract String getJson(String query);

	public Object getResource() {
		return resource;
	}

	public void setResource(Object resource) {
		this.resource = resource;
	}

}
