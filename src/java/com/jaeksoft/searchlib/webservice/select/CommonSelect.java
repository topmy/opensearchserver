/**   
 * License Agreement for OpenSearchServer
 *
 * Copyright (C) 2013 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of OpenSearchServer.
 *
 * OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.webservice.select;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import com.jaeksoft.searchlib.Client;
import com.jaeksoft.searchlib.SearchLibException;
import com.jaeksoft.searchlib.analysis.LanguageEnum;
import com.jaeksoft.searchlib.collapse.CollapseParameters;
import com.jaeksoft.searchlib.facet.FacetField;
import com.jaeksoft.searchlib.facet.FacetFieldList;
import com.jaeksoft.searchlib.filter.FilterAbstract;
import com.jaeksoft.searchlib.filter.FilterList;
import com.jaeksoft.searchlib.filter.QueryFilter;
import com.jaeksoft.searchlib.function.expression.SyntaxError;
import com.jaeksoft.searchlib.query.ParseException;
import com.jaeksoft.searchlib.request.AbstractRequest;
import com.jaeksoft.searchlib.request.RequestTypeEnum;
import com.jaeksoft.searchlib.request.ReturnField;
import com.jaeksoft.searchlib.request.ReturnFieldList;
import com.jaeksoft.searchlib.request.SearchRequest;
import com.jaeksoft.searchlib.snippet.SnippetField;
import com.jaeksoft.searchlib.snippet.SnippetFieldList;
import com.jaeksoft.searchlib.sort.SortField;
import com.jaeksoft.searchlib.sort.SortFieldList;
import com.jaeksoft.searchlib.webservice.CommonServices;

public class CommonSelect extends CommonServices {

	protected AbstractRequest getRequest(Client client, String template,
			RequestTypeEnum type) throws SearchLibException {
		if (template == null || template.length() == 0)
			return null;
		AbstractRequest request = client.getNewRequest(template);
		if (request.getType() != type)
			throw new CommonServiceException("The template " + template
					+ " don't have the expected type: " + type.getLabel());
		return request;
	}

	protected SearchRequest getSearchRequest(Client client, String template,
			String query, Integer start, Integer rows, LanguageEnum lang,
			OperatorEnum operator, String collapseField, Integer collapseMax,
			CollapseParameters.Mode collapseMode,
			CollapseParameters.Type collapseType, List<String> filter,
			List<String> negativeFilter, List<String> sort,
			List<String> returnedField, List<String> snippetField,
			List<String> facet, List<String> facetCollapse,
			List<String> facetMulti, List<String> facetMultiCollapse,
			List<String> filterParams, List<String> joinParams,
			Boolean enableLog, List<String> customLog)
			throws SearchLibException, SyntaxError, IOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, ParseException, URISyntaxException,
			InterruptedException {

		SearchRequest searchRequest = (SearchRequest) getRequest(client,
				template, RequestTypeEnum.SearchRequest);
		if (searchRequest == null)
			searchRequest = new SearchRequest(client);
		searchRequest.setQueryString(query);
		if (start != null)
			searchRequest.setStart(start);
		if (rows != null)
			searchRequest.setRows(rows);
		if (lang != null)
			searchRequest.setLang(lang);
		if (operator != null)
			searchRequest.setDefaultOperator(operator.name());
		if (collapseField != null && !collapseField.equals(""))
			searchRequest.setCollapseField(collapseField);
		if (collapseMax != null)
			searchRequest.setCollapseMax(collapseMax);
		if (collapseMode != null)
			searchRequest.setCollapseMode(collapseMode);
		if (collapseType != null)
			searchRequest.setCollapseType(collapseType);
		if (filter != null && filter.size() > 0) {
			FilterList fl = searchRequest.getFilterList();
			for (String value : filter)
				if (value != null && !value.equals(""))
					if (value.trim().length() > 0)
						fl.add(new QueryFilter(value, false,
								FilterAbstract.Source.REQUEST, null));
		}

		if (negativeFilter != null && negativeFilter.size() > 0) {
			FilterList fl = searchRequest.getFilterList();
			for (String value : negativeFilter)
				if (value != null)
					if (value.trim().length() > 0)
						fl.add(new QueryFilter(value, true,
								FilterAbstract.Source.REQUEST, null));
		}
		if (sort != null && sort.size() > 0) {
			SortFieldList sortFieldList = searchRequest.getSortFieldList();
			for (String value : sort)
				if (value != null && !value.equals(""))
					sortFieldList.put(new SortField(value));
		}
		if (returnedField != null && returnedField.size() > 0) {
			ReturnFieldList rf = searchRequest.getReturnFieldList();
			for (String value : returnedField)
				if (value != null)
					if (value.trim().length() > 0)
						rf.put(new ReturnField(client.getSchema()
								.getFieldList().get(value).getName()));
		}
		if (snippetField != null && snippetField.size() > 0) {
			SnippetFieldList snippetFields = searchRequest
					.getSnippetFieldList();
			for (String value : snippetField)
				if (value != null && !value.equals(""))
					snippetFields.put(new SnippetField(client.getSchema()
							.getFieldList().get(value).getName()));
		}
		if (facet != null && facet.size() > 0) {
			FacetFieldList facetList = searchRequest.getFacetFieldList();
			for (String value : facet)
				if (value != null && !value.equals(""))
					facetList.put(FacetField.buildFacetField(value, false,
							false));
		}
		if (facetCollapse != null && facetCollapse.size() > 0) {
			FacetFieldList facetList = searchRequest.getFacetFieldList();
			for (String value : facetCollapse)
				if (value != null && !value.equals(""))
					facetList.put(FacetField
							.buildFacetField(value, false, true));
		}
		if (facetMulti != null && facetMulti.size() > 0) {
			FacetFieldList facetList = searchRequest.getFacetFieldList();
			for (String value : facetMulti)
				if (value != null && !value.equals(""))
					facetList.put(FacetField
							.buildFacetField(value, true, false));
		}
		if (facetMultiCollapse != null && facetMultiCollapse.size() > 0) {
			FacetFieldList facetList = searchRequest.getFacetFieldList();
			for (String value : facetMultiCollapse)
				if (value != null && !value.equals(""))
					facetList
							.put(FacetField.buildFacetField(value, true, true));
		}
		if (filterParams != null && filterParams.size() > 0) {
			int i = 0;
			for (String param : filterParams)
				searchRequest.getFilterList().setParam(i++, param);
		}
		if (joinParams != null && joinParams.size() > 0) {
			int i = 0;
			for (String param : joinParams)
				searchRequest.getJoinList().setParam(i++, param);
		}

		if (enableLog != null)
			searchRequest.setLogReport(enableLog);

		if (searchRequest.isLogReport())
			for (String logString : customLog)
				if (logString != null && logString.length() > 0)
					searchRequest.addCustomLog(logString);

		return searchRequest;
	}

}
