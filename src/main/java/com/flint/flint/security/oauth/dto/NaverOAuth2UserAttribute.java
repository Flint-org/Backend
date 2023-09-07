package com.flint.flint.security.oauth.dto;

import com.flint.flint.member.domain.main.Member;
import com.flint.flint.member.spec.Gender;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Map;

/**
 * 네이버에서 받아오는 사용자 정보 담는 DTO
 * @Author 정순원
 * @Since 2023-08-19
 */
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class NaverOAuth2UserAttribute extends OAuth2UserAttribute {

    private static final String NAVER_PROVIDER_ID = "naver";

    private Map<String, Object> response;


    @Override
    public Member toEntity() {
        return Member.builder()
                .providerName(NAVER_PROVIDER_ID)
                .providerId(getProviderId())
                .email(getEmail())
                .name(getName())
                .gender(Gender.valueOf(getGender().toUpperCase())) //대소문자 구별하니 바꿔줘야 함
                .birthday(LocalDate.parse(getBirthday()))
                .build();
    }

    @Override
    public String getProviderId() {
        return response.get("id").toString();
    }

    @Override
    public String getName() {
        return response.get("name").toString();
    }

    @Override
    public String getGender() {
        return response.get("gender").toString();
    }

    @Override
    public String getBirthday() {
        return response.get("birthyear").toString();
    }

    @Override
    public String getEmail() {
        return response.get("email").toString();
    }

    @Override
    public void UserAttributesByOAuthToken(OAuth2AccessToken oauth2AccessToken) {


        JSONObject response = WebClient.create()
                .get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(oauth2AccessToken.getAccessToken()))
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();

        Map<String, Object> kakaoAccount = (Map<String, Object>)response.get("response");

    }
}
