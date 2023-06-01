package com.artbridge.gateway.adapter;

/**
 * 회원 이름을 생성하는 서비스 인터페이스입니다.
 */
public interface MemberNameService {
    /**
     * 회원 이름을 생성합니다.
     *
     * @param id 생성할 회원의 식별자 (long)
     */
    void produceMemberName(long id);
}
