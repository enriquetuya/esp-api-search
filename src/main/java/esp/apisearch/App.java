package esp.apisearch;

import java.io.IOException;

import ar.com.asanteit.esp.apps.commons.request.rest.ThrowableMapper;
import ar.com.asanteit.esp.jaxrs.JaxRsContext;
import ar.com.asanteit.esp.spi.Application;
import ar.com.asanteit.esp.spi.ApplicationException;
import ar.com.asanteit.esp.spi.Platform;
import ar.com.asanteit.esp.spi.Request;
import ar.com.asanteit.esp.spi.Response;
import esp.apisearch.controllers.IndexController;
import esp.apisearch.controllers.SchemaController;
import esp.apisearch.controllers.SearchController;

public class App  implements Application {

	private JaxRsContext jaxrs = null;

	@Override
	public void onLoad(Platform platform) throws ApplicationException {
		// Register REST services.
		jaxrs = new JaxRsContext(platform);

		jaxrs.setAttribute("platform", platform);
		jaxrs.registerProvider(ThrowableMapper.class);
		jaxrs.registerProvider(SchemaController.class);
		jaxrs.registerProvider(IndexController.class);
		jaxrs.registerProvider(SearchController.class);
		jaxrs.init();
	}


	@Override
	public void onRequest(Platform platform, Request request, Response response)
	    throws ApplicationException, IOException {
		jaxrs.serviceREST(request, response);
	}


	@Override
  public void onUnload(Platform arg0) throws ApplicationException {
		jaxrs.destroy();
  }

}
