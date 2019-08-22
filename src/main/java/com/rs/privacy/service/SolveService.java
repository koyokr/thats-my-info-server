package com.rs.privacy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rs.privacy.model.SiteAdminContactDTO;
import com.rs.privacy.model.SolveNaverDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SolveService {

    private final static String REFERER_URL = "https://help.naver.com/support/contents/contents.help?serviceNo=964&categoryNo=2826&contentsNo=13342&interactiveMainNo=12600";
    private final static String UMON_CHECK_INTERFACE_URL = "https://help.naver.com/support/umon/umonCheckInterface.help";
    private final static String UMON_ADD_INTERFACE_URL = "https://help.naver.com/support/umon/umonAddInterface.help";
    private final static String REPORT_INQUIRY_INSERT_URL = "https://help.naver.com/support/mail/reportInquiryInsert.help";

    private final static String WHOIS_KISA_URL = "http://whois.kisa.or.kr/";
    private final static String WHOIS_KISA_PUNYCODE_URL = "https://xn--c79as89aj0e29b77z.xn--3e0b707e/";

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    public Boolean inNaver(SolveNaverDTO solveDTO) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Referer", REFERER_URL);

        Document doc = getDocument();
        if (doc == null) {
            return false;
        }

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("contentUrl", solveDTO.getUrl());
        JsonNode node = restTemplate.postForObject(UMON_CHECK_INTERFACE_URL, new HttpEntity<>(map, headers), JsonNode.class);

        String sendData = getSendData(node, solveDTO);
        if (sendData == null) {
            return false;
        }
        map = new LinkedMultiValueMap<>();
        map.add("sendData", sendData);
        map.add("ecmsCategoryNo", "12193");
        map.add("reportToken", doc.getElementById("reportToken").attr("value"));
        restTemplate.postForObject(UMON_ADD_INTERFACE_URL, new HttpEntity<>(map, headers), String.class);

        String commonUrls = getCommonUrls(node, solveDTO);
        if (commonUrls == null) {
            return false;
        }
        List<String> keys = Arrays.asList(
                "formToken", "reportToken", "attachFileNo", "attachFileCount", "categoryNo", "categoryAlias", "divisionExcept", "reply",
                "noteAccept", "reinquiry", "firstUnacceptInquiryID", "serviceType", "location", "maxSizeOption", "hasRealName", "hasPhoneAuth",
                "hasSignAuth", "onlyUmonUpdateFlag", "loggedIn", "itemCount", "answerType", "inquiryTitle",
                "itemInfo[0]", "itemInfo[1]", "itemInfo[2]", "itemInfo[3]", "itemInfo[4]", "itemInfo[5]", "itemInfo[6]", "itemInfo[7]", "itemInfo[8]",
                "itemInfo[9]", "itemInfo[10]", "itemInfo[11]", "itemInfo[12]", "itemInfo[13]", "itemInfo[14]", "itemInfo[15]", "itemInfo[16]",
                "PcInqrPath"
        );
        map = new LinkedMultiValueMap<>();
        addKeysByDoc(map, keys, doc);
        map.add("reportCode", "AA24");
        map.add("communityYn", "N");
        map.add("specialistAnswer", "");
        map.add("customerMemberID", "");
        map.add("customerEmail", solveDTO.getEmail());
        map.add("emailAddressChecked", "");
        map.add("Email1", solveDTO.getEmail().split("@")[0]);
        map.add("Email3", solveDTO.getEmail().split("@")[1]);
        map.add("Email2", "self");
        map.add("notLoginAccept", "N");
        map.add("inquiryContent", "<span id=url>" + solveDTO.getUrl() + "</span><span id=opt>map.add(개인정보침해)</span>\n<span id=cont>" + solveDTO.getDescription() + "</span>\n");
        map.add("service_code", "");
        map.add("report_code", "");
        map.add("umonContent", node.get("content").textValue());
        map.add("umonStatus", node.get("status").textValue());
        map.add("commonUrls", commonUrls);
        map.add("url_input", solveDTO.getUrl());
        map.add("_kin_id_input", "신고하시고자 하는 ID를 입력해주세요");
        map.add("uMONtext01", solveDTO.getDescription());
        map.add("attachCount_reportFile01", "0");
        map.add("attachMaxCount_reportFile01", "3");
        map.add("individualInfoYn", "Y");
        map.add("parentAgreementNeed", "false");
        String result = restTemplate.postForObject(REPORT_INQUIRY_INSERT_URL, new HttpEntity<>(map, headers), String.class);

        return "SUCCESS".equals(result);
    }

    public SiteAdminContactDTO getWhois(String url) {
        Pattern pattern = Pattern.compile("([\\w-]+\\.(?:(?:co|or|pe|ne|re|go|hs|ms|es|kg|sc|ac)\\.)?[\\w-]+)$");

        String host = UriComponentsBuilder.fromHttpUrl(url).build().getHost();
        Matcher matcher = pattern.matcher(host);
        if (!matcher.find()) {
            return null;
        }
        String domain = matcher.group(1);

        String[] ds = domain.split("\\.");
        String tld = ds[ds.length - 1];
        if ("kr".equals(tld) || "xn--3e0b707e".equals(tld)) {
            return getContactFromKr(domain);
        }
        return getContactFromNotKr(domain);
    }

    private void addKeysByDoc(MultiValueMap map, List<String> keys, Document doc) {
        for (String key : keys) {
            Element element = doc.getElementById(key);
            if (element != null) {
                map.add(key, element.attr("value"));
            } else {
                element = doc.getElementsByAttributeValue("name", key).first();
                map.add(key, element.attr("value"));
            }
        }
    }

    private Document getDocument() {
        try {
            return Jsoup.connect(REFERER_URL).get();
        } catch (IOException e) {
            return null;
        }
    }

    private String getSendData(JsonNode node, SolveNaverDTO solveNaverDTO) {
        ObjectNode content = objectMapper.createObjectNode();
        content.put("content", node.get("content").textValue());
        content.put("status", node.get("status").textValue());
        content.put("url", solveNaverDTO.getUrl());
        content.put("reportReason", "personalInformation");
        content.put("reportDesc", solveNaverDTO.getDescription());
        content.put("communityYn", "N");

        ArrayNode contentList = objectMapper.createArrayNode();
        contentList.add(content);

        ObjectNode dataIn = objectMapper.createObjectNode();
        dataIn.put("reportId", "");
        dataIn.put("memberType", "N");
        dataIn.put("reportEmail", solveNaverDTO.getEmail());
        dataIn.set("contentList", contentList);

        try {
            return objectMapper.writeValueAsString(dataIn);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String getCommonUrls(JsonNode node, SolveNaverDTO solveDTO) {
        ObjectNode dataIn = objectMapper.createObjectNode();
        dataIn.put("key", node.get("key").textValue());
        dataIn.put("url", solveDTO.getUrl());
        dataIn.put("urlType", node.get("status").textValue());
        dataIn.put("reportReason", "personalInformation");
        dataIn.put("reportDesc", solveDTO.getDescription());
        dataIn.put("umonContent", node.get("content").textValue());
        dataIn.put("communityYn", "N");
        dataIn.put("writerId", "null");
        dataIn.put("specialistAnswer", "false");
        dataIn.put("duplication", "false");

        ArrayNode arrayIn = objectMapper.createArrayNode();
        arrayIn.add(dataIn);

        try {
            return objectMapper.writeValueAsString(arrayIn);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private SiteAdminContactDTO getContactFromKr(String domain) {
        String whoisUrl = UriComponentsBuilder.fromHttpUrl(WHOIS_KISA_URL)
                .path("/openapi/whois.jsp")
                .queryParam("query", domain)
                .queryParam("key", "2019081917144773968679")
                .queryParam("answer", "json")
                .build().toUriString();

        SiteAdminContactDTO contact = new SiteAdminContactDTO();

        RestTemplate restTemplate = restTemplateBuilder.build();
        String jsonString = restTemplate.getForObject(whoisUrl, String.class);
        JsonNode node;
        try {
            node = objectMapper.readTree(jsonString).get("whois").get("krdomain");
        } catch (IOException e) {
            return contact;
        } catch (NullPointerException e) {
            return contact;
        }

        contact.setName(node.get("adminName").textValue());
        contact.setEmail(node.get("adminEmail").textValue());
        contact.setRegistrar(node.get("agency").textValue());
        return contact;
    }

    private String getMatcherGroupFirst(List<Pattern> patterns, String whoisResult) {
        Matcher matcher;
        for (Pattern pattern : patterns) {
            matcher = pattern.matcher(whoisResult);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private SiteAdminContactDTO getContactFromNotKr(String domain) {
        String whoisUrl = UriComponentsBuilder.fromHttpUrl(WHOIS_KISA_PUNYCODE_URL)
                .path("/kor/whois.jsc").build().toUriString();
        String refererUrl = UriComponentsBuilder.fromHttpUrl(WHOIS_KISA_PUNYCODE_URL)
                .path("/kor/whois/whois.jsp").build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        headers.set("Referer", refererUrl);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("query", domain);

        RestTemplate restTemplate = restTemplateBuilder.build();
        String whoisResult = restTemplate.postForObject(whoisUrl, new HttpEntity<>(map, headers), String.class);

        List<Pattern> namePatterns = Arrays.asList(
                Pattern.compile("Admin Name: (.+)"),
                Pattern.compile("Tech Name: (.+)"),
                Pattern.compile("Admin Organization: (.+)"),
                Pattern.compile("Tech Organization: (.+)"),
                Pattern.compile("Organization: (.+)")
        );
        List<Pattern> emailPatterns = Arrays.asList(
                Pattern.compile("Admin Email: (.+)"),
                Pattern.compile("Tech Email: (.+)"),
                Pattern.compile("Email: (.+)")
        );
        List<Pattern> phonePatterns = Arrays.asList(
                Pattern.compile("Admin Phone: (.+)"),
                Pattern.compile("Tech Phone: (.+)"),
                Pattern.compile("Phone: (.+)")
        );
        List<Pattern> registrarPatterns = Collections.singletonList(
                Pattern.compile("Registrar: (.+)")
        );

        SiteAdminContactDTO contact = new SiteAdminContactDTO();
        contact.setName(getMatcherGroupFirst(namePatterns, whoisResult));
        contact.setEmail(getMatcherGroupFirst(emailPatterns, whoisResult));
        contact.setPhone(getMatcherGroupFirst(phonePatterns, whoisResult));
        contact.setRegistrar(getMatcherGroupFirst(registrarPatterns, whoisResult));
        return contact;
    }
}
