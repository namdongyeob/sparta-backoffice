package com.sparta.backoffice.common.config;

import at.favre.lib.crypto.bcrypt.BCrypt;

import org.springframework.stereotype.Component;

/**
 * 비밀번호 암호화 및 검증을 담당하는 유틸리티 클래스입니다.
 * BCrypt 알고리즘을 사용하여 비밀번호를 해싱합니다.
 */
@Component
public class PasswordEncoder {

	/**
	 * 평문 비밀번호를 BCrypt 알고리즘으로 암호화 합니다.
	 *
	 * @param rawPassword 암호화할 비밀번호
	 * @return 암호화된 비밀번호 문자열
	 */
	public String encode(String rawPassword) {
		return BCrypt.withDefaults().hashToString(BCrypt.MIN_COST, rawPassword.toCharArray());
	}

	/**
	 * 비밀번호가 암호화된 비밀번호와 일치하는지 검증합니다.
	 *
	 * @param rawPassword     검증할 평문 비밀번호
	 * @param encodedPassword 저장되어 있는 암호화된 비밀번호
	 * @return 비밀번호가 일치하면 true, 그렇지 않으면 false
	 */
	public boolean matches(String rawPassword, String encodedPassword) {
		BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword);
		return result.verified;
	}
}