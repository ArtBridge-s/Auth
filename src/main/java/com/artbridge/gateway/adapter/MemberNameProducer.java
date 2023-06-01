package com.artbridge.gateway.adapter;

/**
 * 회원 이름을 처리하는 인터페이스입니다.
 */
public interface MemberNameProducer {
    /**
     * 회원 이름을 처리합니다.
     *
     * @param name 처리할 회원 이름 (String)
     */
    void memberNameProduce(Long id, String name);
}
