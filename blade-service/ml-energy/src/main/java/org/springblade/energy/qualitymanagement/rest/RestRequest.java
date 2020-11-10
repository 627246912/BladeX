package org.springblade.energy.qualitymanagement.rest;

import org.springblade.energy.qualitymanagement.vo.RestRequestParamsVo;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;


public class RestRequest {

	private static final String BLADE_AUTH = "Blade-Auth";
	private static final String AUTHORIZATION = "Authorization";
	private static final String TENANT_ID = "Tenant-Id";
	private static final String GRANT_TYPE = "password";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	private static final String SCOPE = "all";

	public static String post(List<String> uri, Map<String, Object> params) {


		RestRequestParamsVo vo = new RestRequestParamsVo();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		httpHeaders.set(BLADE_AUTH, vo.getBladeAuth());
		httpHeaders.set(AUTHORIZATION, vo.getAuthorization());
		httpHeaders.set(TENANT_ID, vo.getTenantId());

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("grant_type", GRANT_TYPE);
		body.add("username", USERNAME);
		body.add("password", PASSWORD);
		body.add("scope", SCOPE);

		ResponseEntity<Object> responseEntity = new RestTemplate().exchange(uri.get(0), HttpMethod.POST, new HttpEntity<>(body, httpHeaders), Object.class);
		Map<String, String> map = RestRequest.StringToMap(Objects.requireNonNull(responseEntity.getBody()).toString());
		String refreshToken = "bearer " + map.get("refresh_token");
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set(BLADE_AUTH, refreshToken);
		ResponseEntity<Object> exchange = new RestTemplate().exchange(uri.get(1), HttpMethod.POST, new HttpEntity<>(params, httpHeaders), Object.class);
		return exchange.getStatusCodeValue() + "";
	}

	private static Map<String, String> StringToMap(String msg) {

		String[] s1 = msg.replace("{", "").replace("}", "").split(",");
		List<String> list = new ArrayList<>();
		for (String v : s1) {
			String[] s2 = v.split("=");
			list.addAll(Arrays.asList(s2));
		}
		Map<String, String> map = new HashMap<>();
		AtomicReference<String> value = new AtomicReference<>("");
		AtomicReference<String> key = new AtomicReference<>("");
		IntStream.range(0, list.size()).forEachOrdered(i -> {
			String val = list.get(i).trim();
			if (i % 2 == 0) {
				key.set(val);
			} else {
				value.set(val);
			}

			map.put(key.get(), value.get());
		});
		return map;
	}
}
