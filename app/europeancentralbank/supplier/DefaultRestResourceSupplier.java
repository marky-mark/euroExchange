package europeancentralbank.supplier;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import java.net.URI;

public final class DefaultRestResourceSupplier implements RestResourceSupplier {

  private final WebResource resource;

  public DefaultRestResourceSupplier(URI baseUri) {
    resource = createResource(baseUri);
  }

  @Override
  public WebResource get() {
    return resource;
  }

  private WebResource createResource(URI baseUri) {
    ClientConfig clientConfig = new DefaultClientConfig();
    return Client.create(clientConfig).resource(baseUri);
  }

}
