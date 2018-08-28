
/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */package com.cognifide.cq.apm.core.servlets;

import static com.cognifide.cq.apm.core.models.RowModel.ROW_MODEL_RESOURCE_TYPE;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.cognifide.cq.apm.core.Apm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;

@SlingServlet(resourceTypes = {"apm/scripts"}, methods = {"GET"})
@Service
@Properties({
        @Property(name = Constants.SERVICE_DESCRIPTION, value = "APM Scripts Data Source Servlet"),
        @Property(name = Constants.SERVICE_VENDOR, value = Apm.VENDOR_NAME)
})
public class ScriptsDatasourceServlet extends SlingSafeMethodsServlet{

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
       String path =  request.getRequestPathInfo().getSuffix();
        List<Resource> scripts = new ArrayList<>();
        Resource resource = request.getResourceResolver().getResource(path);
        for (Resource child : resource.getChildren()) {
            scripts.add(new ResourceTypeWrapper(child));
        }
        DataSource dataSource = new SimpleDataSource(scripts.iterator());
        request.setAttribute(DataSource.class.getName(), dataSource);
    }

    private class ResourceTypeWrapper extends ResourceWrapper {

        ResourceTypeWrapper(Resource resource){
            super(resource);
        }

        @Override
        public String getResourceType() {
            return ROW_MODEL_RESOURCE_TYPE;
        }
    }

}
